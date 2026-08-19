# RoutePay

**Every ride. One tap.**

A MoMo-powered Mini App that digitises minibus taxi fare payments via QR, plans multi-modal routes, and gives South Africa's informal transport operators a digital footprint for credit and growth.

Built for the **MoMo Mini App Hackathon 2026** — Track 3: Travel and Mobility.

---

## Quick Start

```bash
# Clone the repo
git clone https://github.com/DemainMan/RoutePay.git
cd RoutePay

# Install uv (if not installed)
curl -LsSf https://astral.sh/uv/install.sh | sh

# Install Python + dependencies
uv sync

# Set up environment
cp .env.example .env

# Start the API server
uv run uvicorn routepay_api.main:app --reload

# Seed the database (in another terminal)
uv run python scripts/seed.py
```

Open **http://localhost:8000/docs** for interactive API documentation.

---

## Architecture

```
RoutePay/
├── apps/
│   ├── miniapp/              # React Native (Expo) — MoMo Mini App
│   ├── operator-dashboard/   # Next.js — Operator web dashboard
│   └── landing/              # Next.js — Marketing/pitch page
├── services/
│   └── api/                  # Python 3.11 + FastAPI + SQLAlchemy 2.0
├── packages/
│   ├── momo-sdk/             # Python MoMo API client (importable)
│   └── shared-types/         # Pydantic models shared across services
├── docs/                     # Pitch, decisions, API docs, demo script
├── scripts/                  # Seed data, utilities
└── tests/                    # pytest test suite
```

## MoMo API Integration

| API | Purpose | Status |
|---|---|---|
| Collections API | Fare payments from commuters | Mock mode ready |
| Disbursements API | Instant payouts to operators | Mock mode ready |
| Remittances API | Cross-border migrant corridors | Mock mode ready |
| Payments API | Passes and premium features | Mock mode ready |
| Account Verification | OTP-based phone login | Mock mode ready |

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Python 3.11, FastAPI, SQLAlchemy 2.0 |
| Validation | Pydantic v2 |
| Database | SQLite (dev) / PostgreSQL (prod) |
| Mobile | React Native, Expo, TypeScript |
| Dashboard | Next.js 14, Tailwind, shadcn/ui |
| Testing | pytest, pytest-asyncio |
| Linting | ruff, mypy --strict |
| CI | GitHub Actions |

## Demo

See [docs/DEMO_SCRIPT.md](docs/DEMO_SCRIPT.md) for the 3-minute demo walkthrough.

## License

MIT © 2026 AphileNgubeni
