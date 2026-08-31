# Judge Q&A — Anticipated Questions

## 1. How do you handle network failures during QR payment?

Offline queue with idempotency keys. Payment is queued when offline, synced
when online. User sees "pending" status, gets confirmation when sync completes.
Each payment has a unique idempotency key so retries never double-charge.

## 2. What stops a commuter from sharing a QR code with a friend?

Short-lived QR codes (refresh every 30 seconds), per-trip tokens, and optional
GPS verification at scan time. The QR encodes the driver's MoMo account + a
nonce — sharing it is useless after 30 seconds.

## 3. How will you scale to 1M users?

Async Spring Boot (virtual threads in Java 21 when ready), PostgreSQL read
replicas, Redis caching for route data, CDN for static assets, horizontal
scaling via Kubernetes. The stateless JWT architecture makes scaling
horizontal by design.

## 4. Why is this better than just using the MoMo app directly?

Purpose-built for transit: QR scanning optimized for speed (<2s), route
planning, travel passes, real-time operator dashboard. MoMo is the payment
layer; RoutePay is the mobility layer. Commuters don't need to think about
amounts — the route determines the fare.

## 5. What's your revenue model?

- 2–3% transaction fee on every fare (shared with MTN)
- Premium operator features: advanced analytics, fleet management — R299/month
- Data insights for urban planning (anonymized, opt-in)
- Travel pass commissions

## 6. How do you handle driver/operator fraud?

Trip verification with GPS check-in/check-out, anomaly detection on fare
amounts vs route averages, driver rating system, manual review for
high-value transactions, and MoMo's built-in transaction limits.

## 7. What about cross-border corridors?

Remittances API already integrated. Supports ZAR → ZWL (Zimbabwe) and
ZAR → MZN (Mozambique) for migrant workers. Many taxi routes already cross
borders — we enable digital payments on those corridors.

## 8. How does the operator's micro-loan work?

Transaction history = credit score. After 3 months of consistent revenue
through RoutePay, operators qualify for pre-approved MoMo-backed loans.
The digital trail replaces the informal "word of mouth" credit system.

## 9. What if MoMo is down?

Fallback to USSD payments, queue transactions locally, retry with exponential
backoff, show user "payment pending" status. The operator dashboard shows
pending transactions that need reconciliation.

## 10. Why should MTN partner with you?

- Massive transaction volume: 1.5M daily MoMo transactions if 10% adoption
- Addresses a real pain point for 15M+ daily commuters
- Aligns with MoMo Super App vision — we drive transaction volume
- We've already done the hard work: security, testing, documentation
- Clear path from hackathon MVP to production deployment

## 11. How do you ensure data privacy?

Phone numbers are hashed (SHA-256) before storage. JWT tokens are short-lived
(24h expiry). No payment card data is ever stored — MoMo handles all PCI
compliance. GDPR/POPIA compliant data handling.

## 12. What's your tech stack and why?

Java 17 + Spring Boot — enterprise-grade, strong typing, massive ecosystem.
React Native (Expo) — cross-platform mobile from one codebase.
Next.js — server-rendered dashboard, fast, TypeScript.
All chosen for production viability, not just hackathon convenience.

## 13. Are the MoMo payments real or mocked, and does that matter?

They're mocked for the demo (`momo.environment=MOCK`) so we never move real
money. The SDK is a thin wrapper around the real MoMo Open API — switching to
live is a single config value plus your real subscription key. All 5 API
groups (Collections, Disbursements, Remittances, Payments, Auth) return
realistic responses with UUIDs and timestamps. The mock-first design is what
makes a reliable, repeatable live demo possible.

## 14. How does the operator dashboard get its earnings data?

Every trip and pass purchase is persisted, and the operator endpoints
(`/api/operator/earnings`) aggregate those records by day. The dashboard
fetches this in real time — when a judge books a trip in Swagger, the revenue
figure updates on the dashboard over WebSocket. What you see is live data,
not static mock charts.

