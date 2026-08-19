"""Tests for the RoutePay API endpoints."""

from __future__ import annotations

import pytest
from httpx import ASGITransport, AsyncClient
from routepay_api.database import get_db
from routepay_api.main import app
from routepay_api.models import Base
from sqlalchemy.ext.asyncio import AsyncSession, async_sessionmaker, create_async_engine


@pytest.fixture(autouse=True)
async def setup_db() -> None:
    """Create tables before each test, drop after."""
    engine = create_async_engine("sqlite+aiosqlite://", echo=False)
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

    session_factory = async_sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)

    async def override_get_db():
        async with session_factory() as session:
            try:
                yield session
                await session.commit()
            except Exception:
                await session.rollback()
                raise

    app.dependency_overrides[get_db] = override_get_db
    yield
    app.dependency_overrides.clear()
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all)
    await engine.dispose()


@pytest.fixture
async def client(setup_db: None) -> AsyncClient:
    """Create an async test client with a fresh DB."""
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as ac:
        yield ac


@pytest.fixture
async def auth_headers(client: AsyncClient) -> dict[str, str]:
    """Register a user and return auth headers."""
    await client.post("/api/v1/auth/request-otp", json={"phone": "+27821234567"})
    resp = await client.post(
        "/api/v1/auth/verify-otp",
        json={"phone": "+27821234567", "otp": "1234"},
    )
    token = resp.json()["access_token"]
    return {"Authorization": f"Bearer {token}"}


# ── Health ───────────────────────────────────────────────────────────────────


class TestHealth:
    async def test_root(self, client: AsyncClient) -> None:
        resp = await client.get("/")
        assert resp.status_code == 200
        assert resp.json()["app"] == "RoutePay"

    async def test_health(self, client: AsyncClient) -> None:
        resp = await client.get("/health")
        assert resp.status_code == 200
        assert resp.json()["status"] == "healthy"


# ── Auth ─────────────────────────────────────────────────────────────────────


class TestAuth:
    async def test_request_otp(self, client: AsyncClient) -> None:
        resp = await client.post("/api/v1/auth/request-otp", json={"phone": "+27820000001"})
        assert resp.status_code == 200
        assert "OTP sent" in resp.json()["message"]

    async def test_verify_otp_creates_user(self, client: AsyncClient) -> None:
        await client.post("/api/v1/auth/request-otp", json={"phone": "+27820000002"})
        resp = await client.post(
            "/api/v1/auth/verify-otp",
            json={"phone": "+27820000002", "otp": "1234"},
        )
        assert resp.status_code == 200
        data = resp.json()
        assert "access_token" in data
        assert data["role"] == "COMMUTER"

    async def test_verify_otp_bad_code(self, client: AsyncClient) -> None:
        resp = await client.post(
            "/api/v1/auth/verify-otp",
            json={"phone": "+27820000003", "otp": "12"},
        )
        assert resp.status_code in (400, 422)


# ── Routes ───────────────────────────────────────────────────────────────────


class TestRoutes:
    async def test_list_routes(self, client: AsyncClient) -> None:
        resp = await client.get("/api/v1/routes")
        assert resp.status_code == 200
        assert isinstance(resp.json(), list)

    async def test_get_route_not_found(self, client: AsyncClient) -> None:
        resp = await client.get("/api/v1/routes/99999")
        assert resp.status_code == 404


# ── Trips ────────────────────────────────────────────────────────────────────


class TestTrips:
    async def test_my_trips_empty(self, client: AsyncClient, auth_headers: dict[str, str]) -> None:
        resp = await client.get("/api/v1/trips/me", headers=auth_headers)
        assert resp.status_code == 200
        assert isinstance(resp.json(), list)

    async def test_start_trip_vehicle_not_found(
        self, client: AsyncClient, auth_headers: dict[str, str]
    ) -> None:
        resp = await client.post(
            "/api/v1/trips/start",
            json={"vehicle_id": 99999, "route_id": 1},
            headers=auth_headers,
        )
        assert resp.status_code == 404


# ── Passes ───────────────────────────────────────────────────────────────────


class TestPasses:
    async def test_buy_pass(self, client: AsyncClient, auth_headers: dict[str, str]) -> None:
        resp = await client.post(
            "/api/v1/passes",
            json={"type": "WEEKLY"},
            headers=auth_headers,
        )
        assert resp.status_code == 200
        data = resp.json()
        assert data["type"] == "WEEKLY"
        assert data["is_active"] is True

    async def test_my_passes(self, client: AsyncClient, auth_headers: dict[str, str]) -> None:
        await client.post(
            "/api/v1/passes",
            json={"type": "DAILY"},
            headers=auth_headers,
        )
        resp = await client.get("/api/v1/passes/me", headers=auth_headers)
        assert resp.status_code == 200
        assert len(resp.json()) >= 1


# ── Webhooks ─────────────────────────────────────────────────────────────────


class TestWebhooks:
    async def test_momo_webhook(self, client: AsyncClient) -> None:
        resp = await client.post(
            "/api/v1/webhooks/momo",
            json={"transaction_id": "test-123", "status": "SUCCESSFUL"},
        )
        assert resp.status_code == 200
        assert resp.json()["status"] == "received"
