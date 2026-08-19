# MoMo API Integration Notes

## Overview

RoutePay integrates all five MTN MoMo Open API modules to provide a comprehensive
digital payment solution for South Africa's minibus taxi industry.

## API Modules

### 1. Collections API
- **Purpose:** Fare payments from commuters
- **Flow:** Commuter scans QR → MoMo requests payment → Funds deducted → Operator notified
- **Endpoint:** `POST /collection/v1_0/requesttopay`
- **Used in:** QR fare payments, trip completion

### 2. Disbursements API
- **Purpose:** Instant payouts to operators
- **Flow:** Operator requests payout → MoMo transfers funds → Operator receives money
- **Endpoint:** `POST /disbursement/v1_0/transfer`
- **Used in:** Operator dashboard "Payout Now" button

### 3. Remittances API
- **Purpose:** Cross-border migrant corridor transfers
- **Flow:** User sends money cross-border → MoMo handles conversion → Recipient receives
- **Endpoint:** `POST /remittance/v1_0/transfer`
- **Used in:** Family wallet feature for migrant workers

### 4. Payments API
- **Purpose:** Pass purchases and premium features
- **Flow:** User buys travel pass → MoMo deducts → Pass activated
- **Endpoint:** `POST /payment/v1_0/requesttopay`
- **Used in:** Daily/weekly/monthly pass purchases

### 5. Account Verification (Auth)
- **Purpose:** OTP-based phone login
- **Flow:** User enters phone → MoMo sends OTP → User verifies → JWT issued
- **Endpoints:** `POST /collection/token/` (token), verification endpoints
- **Used in:** App login, sensitive operations

## Mock vs Live Mode

| Environment | `MOMO_ENV` | Behavior |
|---|---|---|
| Development | `mock` | Realistic fake responses, no API calls |
| Staging | `sandbox` | Real MoMo sandbox, test credentials |
| Production | `live` | Real MoMo API, live credentials |

To switch from mock to live:
1. Set `MOMO_ENV=live` in `.env`
2. Add real MoMo API credentials (`MOMO_API_USER`, `MOMO_API_KEY`, `MOMO_SUBSCRIPTION_KEY`)
3. Update callback URLs to your production domain
4. That's it — the SDK handles the rest

## Authentication

MoMo API uses Basic Auth for token generation:
```
Authorization: Basic base64(api_user:api_key)
```

The SDK manages token refresh automatically in live mode.

## Callbacks/Webhooks

MoMo sends asynchronous notifications for transaction status updates.
RoutePay handles these at `POST /api/v1/webhooks/momo` and updates
the transaction status in the database.

## Error Handling

The SDK raises specific exceptions:
- `MoMoAPIError` — API returned a non-success status
- `MoMoAuthError` — Authentication failed
- `MoMoConnectionError` — Network connectivity issue
- `MoMoTimeoutError` — Request timed out
