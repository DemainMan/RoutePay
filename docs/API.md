# RoutePay — MoMo API Integration Guide

## Overview

RoutePay integrates all five MoMo Open API groups via a custom Java SDK
(`packages/momo-sdk`). The SDK is environment-aware and supports three
modes:

| Mode | `momo.environment` | Use Case |
|------|---------------------|----------|
| Mock | `MOCK` | Development, demos, testing |
| Sandbox | `SANDBOX` | Pre-production validation |
| Production | `PRODUCTION` | Live MTN MoMo API |

## API Groups

### 1. Authentication (`AuthClient`)

- **Purpose**: Phone-based OTP login for commuters
- **Flow**: Request OTP → verify OTP → receive JWT token
- **Endpoint**: `/api/auth/otp/request`, `/api/auth/otp/verify`
- **Mock behavior**: Returns a random 6-digit OTP on every request

### 2. Collections (`CollectionsClient`)

- **Purpose**: Collect fare payments from commuter wallets
- **MoMo operation**: `requestToPay` — charges the payer's MoMo wallet
- **Used by**: Trip booking (`POST /api/trips`)
- **Mock behavior**: Returns `SUCCESSFUL` immediately with a generated UUID

### 3. Payments (`PaymentsClient`)

- **Purpose**: Purchase travel passes (daily/weekly/monthly)
- **MoMo operation**: Single payment for pass purchase
- **Used by**: Pass purchase (`POST /api/passes`)
- **Mock behavior**: Returns `SUCCESSFUL` immediately

### 4. Disbursements (`DisbursementsClient`)

- **Purpose**: Pay out earnings to operator/driver MoMo wallets
- **MoMo operation**: `transfer` — sends money from platform to driver
- **Used by**: Operator payout (ready for production use)
- **Mock behavior**: Returns `SUCCESSFUL` immediately

### 5. Remittances (`RemittancesClient`)

- **Purpose**: Cross-border money transfers via taxi corridors
- **MoMo operation**: `transfer` — cross-border remittance
- **Used by**: Future feature — migrant workers sending money home
- **Mock behavior**: Returns `SUCCESSFUL` immediately

## SDK Architecture

```
MoMoClient (entry point)
├── getAuth()          → AuthClient
├── getCollections()   → CollectionsClient
├── getPayments()      → PaymentsClient
├── getDisbursements() → DisbursementsClient
├── getRemittances()   → RemittancesClient
└── getMockBackend()   → MockMoMoBackend
```

In MOCK mode, each client short-circuits to `MockMoMoBackend` — no HTTP
calls are made. This makes the demo reliable and instant.

## Configuration

```yaml
momo:
  environment: MOCK                    # MOCK | SANDBOX | PRODUCTION
  api-version: v1_0
  collection-url: https://sandbox.momodeveloper.mtn.com
  disbursement-url: https://sandbox.momodeveloper.mtn.com
  remittance-url: https://sandbox.momodeveloper.mtn.com
  payments-url: https://sandbox.momodeveloper.mtn.com
  subscription-key: ${MOMO_SUBSCRIPTION_KEY:mock-subscription-key}
  callback-host: ${MOMO_CALLBACK_HOST:http://localhost:8080}
```

## Swapping to Live MoMo

To switch from mock to live:

1. Get MoMo API credentials from [developer.mtn.com](https://developer.mtn.com)
2. Set environment variables:
   ```bash
   export MOMO_ENVIRONMENT=SANDBOX
   export MOMO_SUBSCRIPTION_KEY=your-real-subscription-key
   ```
3. Restart the API — SDK connects to real MoMo sandbox

For production, change to `PRODUCTION` and use production credentials.

## Error Handling

The SDK defines three exception types:

| Exception | When |
|-----------|------|
| `MoMoApiException` | MoMo API returns an error response |
| `MoMoAuthException` | Authentication/token generation fails |
| `MoMoConnectionException` | Network timeout or connection failure |

All exceptions are caught by `GlobalExceptionHandler` and returned as
structured JSON error responses.
