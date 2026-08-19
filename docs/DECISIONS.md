# Decisions Log

All design decisions, assumptions, and trade-offs are recorded here.

## 2026-08-19 — Initial Setup

| Decision | Choice | Rationale |
|---|---|---|
| Python version | 3.11+ | Required for `StrEnum`, `match` statements, modern typing |
| Package manager | uv | 10-100x faster than pip, workspace support for monorepo |
| Backend framework | FastAPI | Async-first, auto OpenAPI docs at `/docs`, Pydantic v2 native |
| ORM | SQLAlchemy 2.0 async | Industry standard, `Mapped[]` type hints, Alembic migration support |
| Database (dev) | SQLite via aiosqlite | Zero setup, file-based, easy to reset |
| Database (prod) | PostgreSQL ready | Swap `DATABASE_URL` — show path to production |
| Validation | Pydantic v2 | Type-safe, fast, native FastAPI integration |
| HTTP client | httpx async | Modern, supports async context managers, MoMo API friendly |
| Auth | JWT + phone OTP | Standard for mobile apps; mock OTP provider for hackathon |
| Logging | structlog | Structured, readable, production-grade |
| Monorepo | uv workspaces | Keep momo-sdk as a separate installable package |
| License | MIT | Permissive, hackathon-friendly |
