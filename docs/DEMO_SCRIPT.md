# RoutePay — Demo Script (3 minutes)

## Setup (before demo — 2 minutes)

```bash
# Terminal 1: Backend
bash scripts/demo.sh

# Terminal 2: Operator Dashboard
cd apps/operator-dashboard && npm install && npm run dev
# → http://localhost:3001

# Terminal 3: Landing Page
cd apps/landing && npm install && npm run dev
# → http://localhost:3002
```

Open browser tabs:
- http://localhost:8080/swagger-ui.html
- http://localhost:3001
- http://localhost:3002

---

## The Demo (3 minutes)

### 1. Open with the Problem (15 sec)

> "15 million South Africans commute by minibus taxi every day. 90% pay cash.
> Operators have no digital history. No receipts, no credit access, no safety.
> RoutePay fixes this with MoMo."

### 2. Show the Operator Dashboard (30 sec)

Open http://localhost:3001

> "This is the operator dashboard. Real-time fleet view — today's trips,
> total earnings, active routes. Every number here is a MoMo transaction.
> Let me show you how."

### 3. Show Swagger UI (30 sec)

Open http://localhost:8080/swagger-ui.html

> "All five MoMo APIs are integrated: Collections for fare payments,
> Disbursements for operator payouts, Payments for travel passes,
> Remittances for cross-border corridors, and Authentication for OTP login.
> Let me walk through a complete transaction flow."

### 4. Live Demo — Request OTP (15 sec)

Click on `POST /api/auth/otp/request` → Try it out → Execute

> "Commuter enters their phone number. They receive an OTP — in production
> this comes via SMS. First-time users are registered automatically — zero
> friction onboarding."

### 5. Live Demo — Verify OTP (15 sec)

Copy the OTP from response, paste into `POST /api/auth/otp/verify` → Execute

> "They get a JWT token. Every subsequent action is authenticated and
> tied to their MoMo wallet."

### 6. Live Demo — Browse Routes (10 sec)

Click on `GET /api/routes` → Execute

> "Seven seeded Joburg routes — CBD to Soweto, Sandton to Midrand,
> each with real fares."

### 7. Live Demo — Book a Trip (20 sec)

Copy the JWT token, use in `POST /api/trips` → Execute

> "Trip booked. The MoMo Collections API just charged R15 from the
> commuter's wallet. Payment confirmed in under 2 seconds.
> Let me show you it appeared on the dashboard."

### 8. Switch to Dashboard (15 sec)

Open http://localhost:3001 → refresh

> "There it is — live on the dashboard. The operator sees the trip,
> the route, the fare, and the payment status. All real-time via WebSocket."

### 9. Close with Impact (10 sec)

> "If 10% of SA commuters adopt RoutePay, that's 1.5 million MoMo
> transactions per day. Safer commutes. Digital operators. The future
> of African mobility — powered by MoMo."

---

## Quick Reference — Copy-Paste Commands

```bash
# 1. Request OTP
curl -X POST http://localhost:8080/api/auth/otp/request \
  -H 'Content-Type: application/json' \
  -d '{"phone": "+27821234567"}'

# 2. Verify OTP (replace OTP_VALUE with actual OTP from step 1)
curl -X POST http://localhost:8080/api/auth/otp/verify \
  -H 'Content-Type: application/json' \
  -d '{"phone": "+27821234567", "otp": "OTP_VALUE"}'

# 3. Browse routes
curl http://localhost:8080/api/routes

# 4. Book trip (replace TOKEN_VALUE)
curl -X POST http://localhost:8080/api/trips \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer TOKEN_VALUE' \
  -d '{"routeId": 1}'

# 5. Buy pass
curl -X POST http://localhost:8080/api/passes \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer TOKEN_VALUE' \
  -d '{"passType": "DAILY"}'

# 6. Check stats
curl http://localhost:8080/api/operator/stats
```

## Tips

- Keep dashboard and Swagger side by side
- Book a trip in Swagger, let judges see it appear live on dashboard
- Mention "mock mode — same code ships to production with one env var change"
- If anything fails, say "that's the beauty of live demos" and move on
