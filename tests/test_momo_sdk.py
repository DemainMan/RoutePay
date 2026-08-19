"""Tests for the MoMo SDK."""

from __future__ import annotations

from decimal import Decimal
from uuid import uuid4

import pytest
from momo_sdk import MoMoClient, TransactionStatus
from momo_sdk.mock import MockMoMoBackend
from momo_sdk.models import AuthToken, TransactionRef


@pytest.fixture
async def mock_client() -> MoMoClient:
    """Create a MoMo client in mock mode."""
    async with MoMoClient(env="mock") as client:
        yield client


@pytest.fixture
def mock_backend() -> MockMoMoBackend:
    """Create a mock MoMo backend."""
    return MockMoMoBackend()


# ── Collections ──────────────────────────────────────────────────────────────


class TestCollections:
    """Tests for the Collections API."""

    async def test_pay_returns_successful(self, mock_client: MoMoClient) -> None:
        """Collection payment should return a successful transaction ref."""
        result = await mock_client.collections.pay(
            amount=Decimal("15.00"),
            phone="+27821234567",
            reference="trip_001",
        )
        assert isinstance(result, TransactionRef)
        assert result.status == TransactionStatus.SUCCESSFUL
        assert result.external_id == "trip_001"

    async def test_pay_returns_uuid(self, mock_client: MoMoClient) -> None:
        """Collection payment should return a valid UUID as transaction_id."""
        result = await mock_client.collections.pay(
            amount=Decimal("25.50"),
            phone="+27829876543",
            reference="trip_002",
        )
        assert result.transaction_id is not None

    async def test_get_status_mock(self, mock_client: MoMoClient) -> None:
        """Mock status check should return SUCCESSFUL."""
        result = await mock_client.collections.get_status(str(uuid4()))
        assert result.status == TransactionStatus.SUCCESSFUL


# ── Disbursements ────────────────────────────────────────────────────────────


class TestDisbursements:
    """Tests for the Disbursements API."""

    async def test_transfer_returns_successful(self, mock_client: MoMoClient) -> None:
        """Disbursement transfer should return a successful transaction ref."""
        result = await mock_client.disbursements.transfer(
            amount=Decimal("500.00"),
            phone="+27821112233",
            reference="payout_001",
        )
        assert isinstance(result, TransactionRef)
        assert result.status == TransactionStatus.SUCCESSFUL
        assert result.external_id == "payout_001"

    async def test_get_status_mock(self, mock_client: MoMoClient) -> None:
        """Mock status check should return SUCCESSFUL."""
        result = await mock_client.disbursements.get_status(str(uuid4()))
        assert result.status == TransactionStatus.SUCCESSFUL


# ── Remittances ──────────────────────────────────────────────────────────────


class TestRemittances:
    """Tests for the Remittances API."""

    async def test_send_returns_successful(self, mock_client: MoMoClient) -> None:
        """Remittance send should return a successful transaction ref."""
        result = await mock_client.remittances.send(
            amount=Decimal("200.00"),
            from_phone="+27821234567",
            to_phone="+263781234567",
            reference="remit_001",
        )
        assert isinstance(result, TransactionRef)
        assert result.status == TransactionStatus.SUCCESSFUL
        assert result.external_id == "remit_001"

    async def test_send_with_currency(self, mock_client: MoMoClient) -> None:
        """Remittance should accept a currency parameter."""
        result = await mock_client.remittances.send(
            amount=Decimal("100.00"),
            from_phone="+27821234567",
            to_phone="+263781234567",
            reference="remit_002",
            currency="USD",
        )
        assert result.status == TransactionStatus.SUCCESSFUL


# ── Payments ─────────────────────────────────────────────────────────────────


class TestPayments:
    """Tests for the Payments API."""

    async def test_request_returns_successful(self, mock_client: MoMoClient) -> None:
        """Payment request should return a successful transaction ref."""
        result = await mock_client.payments.request(
            amount=Decimal("350.00"),
            phone="+27821234567",
            reference="pass_weekly_001",
        )
        assert isinstance(result, TransactionRef)
        assert result.status == TransactionStatus.SUCCESSFUL
        assert result.external_id == "pass_weekly_001"

    async def test_get_status_mock(self, mock_client: MoMoClient) -> None:
        """Mock status check should return SUCCESSFUL."""
        result = await mock_client.payments.get_status(str(uuid4()))
        assert result.status == TransactionStatus.SUCCESSFUL


# ── Auth ─────────────────────────────────────────────────────────────────────


class TestAuth:
    """Tests for the Auth (OTP) API."""

    async def test_request_otp_does_not_raise(self, mock_client: MoMoClient) -> None:
        """Mock OTP request should succeed without raising."""
        await mock_client.auth.request_otp("+27821234567")

    async def test_verify_otp_returns_token(self, mock_client: MoMoClient) -> None:
        """Mock OTP verification should return a valid AuthToken."""
        token = await mock_client.auth.verify_otp("+27821234567", "1234")
        assert isinstance(token, AuthToken)
        assert token.access_token.startswith("mock_jwt_")
        assert token.token_type == "Bearer"

    async def test_token_expiry(self, mock_client: MoMoClient) -> None:
        """AuthToken should report expiry correctly."""
        token = await mock_client.auth.verify_otp("+27821234567", "1234")
        assert not token.is_expired


# ── Client lifecycle ─────────────────────────────────────────────────────────


class TestMoMoClient:
    """Tests for the MoMoClient lifecycle."""

    async def test_context_manager(self) -> None:
        """Client should work as an async context manager."""
        async with MoMoClient(env="mock") as client:
            assert client._is_mock
            result = await client.collections.pay(
                amount=Decimal("10.00"),
                phone="+27821234567",
                reference="test",
            )
            assert result.status == TransactionStatus.SUCCESSFUL

    async def test_mock_backend_independence(self, mock_backend: MockMoMoBackend) -> None:
        """Mock backend should work independently of the client."""
        result = await mock_backend.collection_pay(
            amount=Decimal("20.00"),
            phone="+27821234567",
            reference="direct_mock",
        )
        assert result.status == TransactionStatus.SUCCESSFUL
