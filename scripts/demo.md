# RoutePay — Hackathon Demo Guide

Step-by-step walkthrough for presenting RoutePay to judges: OTP login, route
browsing, trip booking, travel passes, the operator dashboard, and real-time
WebSocket updates.

> **All MTN MoMo API calls are mocked by default** (`momo.environment=MOCK`).
> Every payment returns `SUCCESSFUL` instantly — no real money moves, no MoMo
> credentials needed.

---

## Prerequisites

| Tool   | Version | Check              |
| ------ | ------- | ------------------ |
| Java   | 17+     | `java -version`    |
| Maven  | 3.8+    | `mvn -version`     |
| Node   | 20+     | `node -v`          |
| curl   | any     | `curl --version`   |
| jq     | any     | `jq --version` (optional, for token extraction) |

## Quick Start

```bash
bash scripts/demo.sh
```

The script builds the backend, starts it on port **8080**, waits for it to be
healthy, prints the demo flow, and streams logs. Press `Ctrl+C` when done —
it kills the backend automatically.

For the full stack (dashboard + landing page), open two extra terminals:

```bash
# Terminal 2 — Operator dashboard on http://localhost:3000
cd apps/operator-dashboard && npm install && npm run dev

# Terminal 3 — Landing page on http://localhost:3001
cd apps/landing && npm install && npm run dev
```

Or run everything in Docker:

```bash
docker compose up --build
```

---

## Demo Flow

### Step 0 — Show Swagger UI

Open <http://localhost:8080/swagger-ui.html>.

Point out the four tag groups: **Authentication**, **Routes**, **Trips**,
**Travel Passes** — the whole product surface in one screen.

### Step 1 — Request an OTP

No passwords: commuters log in with their phone number and an MTN MoMo OTP.

```bash
OTP=$(curl -s -X POST http://localhost:8080/api/auth/otp/request \
  -H 'Content-Type: application/json' \
  -d '{"phone": "+27821234567"}' \
  | grep -o '"otp":"[^"]*"' | cut -d'"' -f4)

echo "$OTP"
```

Response:

```json
{ "message": "OTP sent", "phone": "+27821234567", "otp": "482916" }
```

### Step 2 — Verify OTP and get a JWT

Use the OTP returned in Step 1:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/otp/verify \
  -H 'Content-Type: application/json' \
  -d "{\"phone\": \"+27821234567\", \"otp\": \"$OTP\"}" \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "$TOKEN"
```

(No jq? Use `| grep -o '"token":"[^"]*"' | cut -d'"' -f4` instead.)

Response contains a JWT plus the nested user profile:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "phoneNumber": "+27821234567",
    "name": "Commuter 4567",
    "role": "COMMUTER"
  }
}
```

First-time users are registered automatically — mention this frictionless
onboarding.

### Step 3 — Browse routes (7 seeded Joburg routes)

```bash
curl http://localhost:8080/api/routes
```

Seeded routes (IDs 1–7):

| ID | Route                          | Fare   |
| -- | ------------------------------ | ------ |
| 1  | Joburg CBD → Soweto            | R15.00 |
| 2  | Sandton → Midrand              | R22.00 |
| 3  | Braamfontein → Rosebank        | R12.00 |
| 4  | Tembisa → Pretoria CBD         | R35.00 |
| 5  | Alexandra → Sandton            | R10.00 |
| 6  | Randburg → Roodepoort          | R20.00 |
| 7  | Vereeniging → Johannesburg CBD | R45.00 |

Drill into one:

```bash
curl http://localhost:8080/api/routes/1
```

### Step 4 — Book a trip (MoMo Collections API — mocked)

Booking a taxi trip collects the fare from the commuter's MoMo wallet via the
**Collections API**:

```bash
curl -X POST http://localhost:8080/api/trips \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"routeId": 1}'
```

Response shows the fare payment settled instantly:

```json
{
  "id": 1,
  "routeName": "Joburg CBD to Soweto",
  "fare": 15.00,
  "status": "CONFIRMED",
  "paymentStatus": "SUCCESSFUL"
}
```

Show trip history too:

```bash
curl http://localhost:8080/api/trips -H "Authorization: Bearer $TOKEN"
```

### Step 5 — Purchase a travel pass (MoMo Payments API — mocked)

Unlimited rides with a daily/weekly/monthly pass, charged via the **Payments
API**: `DAILY` = R25, `WEEKLY` = R99, `MONTHLY` = R350.

```bash
curl -X POST http://localhost:8080/api/passes \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"passType": "DAILY"}'
```

List active passes:

```bash
curl http://localhost:8080/api/passes -H "Authorization: Bearer $TOKEN"
```

### Step 6 — Operator dashboard

Open <http://localhost:3000>.

Highlight for judges:

- Live fleet view of all 7 routes
- Trip volume and revenue from MoMo collections
- Pass sales breakdown (daily / weekly / monthly)

### Step 7 — Real-time trip updates via WebSocket

The backend exposes a STOMP-over-SockJS endpoint at `/ws`; clients subscribe
to `/topic/trips`. New bookings are broadcast the moment they happen.

Quick browser check — paste in DevTools console on any page served from
`localhost:8080` (e.g. Swagger UI):

```js
const socket = new SockJS('http://localhost:8080/ws');
const stomp = Stomp.over(socket);
stomp.connect({}, () => {
  stomp.subscribe('/topic/trips', (msg) => console.log('LIVE:', JSON.parse(msg.body)));
});
```

Then book another trip (Step 4) and watch the event arrive instantly.
From a terminal, `npx wscat -c ws://localhost:8080/ws` also works for a raw
connection check.

**Demo tip:** keep the dashboard and Swagger side by side; book a trip in
Swagger and let judges see it appear live on the dashboard.

---

## Talking Points

- **Why MoMo:** 60M+ MTN MoMo wallets across Africa; commuters pay without
  bank cards or cash — exactly how minibus taxis already work.
- **Mock-first architecture:** the `momo-sdk` package swaps between
  `MOCK`, `SANDBOX`, and `PRODUCTION` environments via one config value, so
  the same code that demos today ships tomorrow.
- **Security:** phone-number + OTP auth issues short-lived JWTs; every
  booking/pass endpoint requires a valid token.
- **Real-time:** operators see trips the second they're paid for.

## Troubleshooting

| Symptom                       | Fix                                                        |
| ----------------------------- | ---------------------------------------------------------- |
| Port 8080 already in use      | `lsof -ti:8080 \| xargs kill` then rerun                   |
| `mvn: command not found`      | Install Maven 3.8+ or use `docker compose up`              |
| Dashboard blank / CORS errors | Ensure the API is running before starting the dashboard    |
| Want real MoMo sandbox calls  | Set `MOMO_ENVIRONMENT=SANDBOX` + keys in `.env`            |
