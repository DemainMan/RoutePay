#!/usr/bin/env bash
#
# ============================================================================
#  RoutePay — Hackathon Demo Launcher
# ============================================================================
#  Builds the Spring Boot backend, starts it in the background, waits until
#  it is healthy, then prints the demo flow for the presenter.
#
#  Usage:
#      bash scripts/demo.sh          # or ./scripts/demo.sh
#
#  Press Ctrl+C when finished — the trap below kills the backend.
#
#  Requirements: Java 17, Maven 3.8+, curl
# ============================================================================

set -euo pipefail

# ----------------------------------------------------------------------------
# Resolve repo root relative to this script so it works from any directory.
# ----------------------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

API_PORT=8080
API_URL="http://localhost:${API_PORT}"
HEALTH_URL="${API_URL}/actuator/health"
LOG_FILE="/tmp/routepay-api.log"
API_PID=""

# ----------------------------------------------------------------------------
# Cleanup trap — always stop the backend, even on Ctrl+C or errors.
# ----------------------------------------------------------------------------
cleanup() {
  if [[ -n "${API_PID}" ]] && kill -0 "${API_PID}" 2>/dev/null; then
    echo ""
    echo "==> Stopping RoutePay API (pid ${API_PID})..."
    kill "${API_PID}" 2>/dev/null || true
    # Give it a moment to shut down gracefully.
    for _ in $(seq 1 10); do
      kill -0 "${API_PID}" 2>/dev/null || break
      sleep 0.5
    done
    kill -9 "${API_PID}" 2>/dev/null || true
    echo "==> Backend stopped. Thanks for watching!"
  fi
}
trap cleanup EXIT INT TERM

# ----------------------------------------------------------------------------
# Sanity checks
# ----------------------------------------------------------------------------
command -v mvn >/dev/null 2>&1 || { echo "ERROR: mvn (Maven) not found. Install Maven 3.8+."; exit 1; }
command -v java >/dev/null 2>&1 || { echo "ERROR: java not found. Install Java 17."; exit 1; }

JAVA_MAJOR="$(java -version 2>&1 | head -n1 | sed -E 's/.*version "([0-9]+).*/\1/')"
if [[ "${JAVA_MAJOR}" -lt 17 ]]; then
  echo "ERROR: Java 17+ required (found ${JAVA_MAJOR})."
  exit 1
fi

# ----------------------------------------------------------------------------
# 1. Build the backend (parent POM builds momo-sdk + api modules).
#    -DskipTests keeps the demo fast; -q keeps output clean.
# ----------------------------------------------------------------------------
echo "==> Building RoutePay backend (this may take a minute)..."
(cd "${ROOT_DIR}" && mvn clean install -DskipTests -q)
echo "==> Build complete."

# Locate the bootable jar produced by spring-boot-maven-plugin.
JAR_FILE="$(ls "${ROOT_DIR}"/services/api/target/routepay-api-*.jar 2>/dev/null | head -n1)"
if [[ -z "${JAR_FILE}" ]]; then
  echo "ERROR: Could not find routepay-api jar in services/api/target/"
  exit 1
fi
echo "==> Jar: ${JAR_FILE}"

# ----------------------------------------------------------------------------
# 2. Start the API in the background.
#    Default profile runs on H2 (in-memory) with seeded Joburg routes and
#    MoMo in MOCK mode — zero external dependencies for the demo.
# ----------------------------------------------------------------------------
echo "==> Starting API on port ${API_PORT} (logs: ${LOG_FILE})..."
java -jar "${JAR_FILE}" >"${LOG_FILE}" 2>&1 &
API_PID=$!

# ----------------------------------------------------------------------------
# 3. Poll the actuator health endpoint until the app is ready (max ~60s).
# ----------------------------------------------------------------------------
echo -n "==> Waiting for API to become healthy"
READY=0
for _ in $(seq 1 60); do
  if curl -fsS "${HEALTH_URL}" >/dev/null 2>&1; then
    READY=1
    break
  fi
  # Fail fast if the JVM died during startup.
  if ! kill -0 "${API_PID}" 2>/dev/null; then
    echo ""
    echo "ERROR: API process exited unexpectedly. Last log lines:"
    tail -n 30 "${LOG_FILE}" || true
    exit 1
  fi
  echo -n "."
  sleep 1
done
echo ""

if [[ "${READY}" -ne 1 ]]; then
  echo "ERROR: API did not become healthy within 60s. Check ${LOG_FILE}"
  exit 1
fi

echo "==> API is UP: ${API_URL}"

# ----------------------------------------------------------------------------
# 4. Print the demo flow for the presenter.
#    Full judge-facing walkthrough lives in scripts/demo.md.
# ----------------------------------------------------------------------------
cat <<EOF

============================================================================
  RoutePay DEMO — press Ctrl+C here when done to stop the backend
============================================================================

  Swagger UI ....... http://localhost:${API_PORT}/swagger-ui.html
  Health ........... ${HEALTH_URL}

  Demo flow (details + copy-paste curls in scripts/demo.md):
    1. Open Swagger UI and browse the endpoints
    2. Request an OTP:
         curl -X POST ${API_URL}/api/auth/otp/request \\
              -H 'Content-Type: application/json' \\
              -d '{"phone": "+27821234567"}'
    3. Verify OTP (mock code is ALWAYS 123456) to get a JWT:
         curl -X POST ${API_URL}/api/auth/otp/verify \\
              -H 'Content-Type: application/json' \\
              -d '{"phone": "+27821234567", "otp": "123456"}'
    4. Browse the 7 seeded Joburg routes:
         curl ${API_URL}/api/routes
    5. Book a trip (MoMo Collections API — mocked):
         curl -X POST ${API_URL}/api/trips \\
              -H 'Content-Type: application/json' \\
              -H 'Authorization: Bearer <TOKEN>' \\
              -d '{"routeId": 1}'
    6. Buy a travel pass (MoMo Payments API — mocked):
         curl -X POST ${API_URL}/api/passes \\
              -H 'Content-Type: application/json' \\
              -H 'Authorization: Bearer <TOKEN>' \\
              -d '{"passType": "DAILY"}'
    7. Operator dashboard (separate terminal):
         cd apps/operator-dashboard && npm run dev   ->  http://localhost:3001
    8. Real-time trip updates over WebSocket (STOMP/SockJS):
         ws://localhost:${API_PORT}/ws   ->  subscribe to /topic/trips

  All MTN MoMo calls are MOCKED by default (momo.environment=MOCK) —
  no real money moves, every payment returns SUCCESSFUL instantly.

============================================================================

==> Streaming API logs below. Ctrl+C to quit.

EOF

# ----------------------------------------------------------------------------
# Stay in the foreground so the presenter sees logs; Ctrl+C triggers cleanup.
# ----------------------------------------------------------------------------
tail -f "${LOG_FILE}" &
TAIL_PID=$!
wait "${API_PID}"
kill "${TAIL_PID}" 2>/dev/null || true
