"""Mock backend for MoMo SDK — realistic responses without hitting real APIs.

Used during hackathon demos and development. Every mock response includes
realistic latency (200-800ms), proper UUIDs, and realistic status codes.
"""

from __future__ import annotations

import asyncio
import random
from datetime import datetime
from decimal import Decimal
from uuid import uuid4

import structlog

from momo_sdk.models import (
    AuthToken,
    TransactionRef,
    TransactionStatus,
)

logger = structlog.get_logger()


class MockMoMoBackend:
    """Simulates the MoMo API backend for hackathon demos.

    Responses are realistic: proper UUIDs, timestamps, latency simulation.
    Toggle via MOMO_ENV=mock in .env.
    """

    async def simulate_latency(self) -> None:
        """Simulate realistic network latency (200-800ms)."""
        delay = random.uniform(0.2, 0.8)
        await asyncio.sleep(delay)

    async def collection_pay(
        self,
        amount: Decimal,
        phone: str,
        reference: str,
        callback_url: str | None = None,
    ) -> TransactionRef:
        """Mock collection payment — fare from commuter."""
        await self.simulate_latency()
        tx_id = str(uuid4())
        logger.info(
            "🚀 Mock Collection",
            amount=str(amount),
            phone=phone,
            reference=reference,
            transaction_id=tx_id,
        )
        return TransactionRef(
            transaction_id=tx_id,
            external_id=reference,
            status=TransactionStatus.SUCCESSFUL,
            created_at=datetime.utcnow(),
        )

    async def disbursement_transfer(
        self,
        amount: Decimal,
        phone: str,
        reference: str,
        callback_url: str | None = None,
    ) -> TransactionRef:
        """Mock disbursement — payout to operator."""
        await self.simulate_latency()
        tx_id = str(uuid4())
        logger.info(
            "🚀 Mock Disbursement",
            amount=str(amount),
            phone=phone,
            reference=reference,
            transaction_id=tx_id,
        )
        return TransactionRef(
            transaction_id=tx_id,
            external_id=reference,
            status=TransactionStatus.SUCCESSFUL,
            created_at=datetime.utcnow(),
        )

    async def remittance_send(
        self,
        amount: Decimal,
        currency: str,
        from_phone: str,
        to_phone: str,
        reference: str,
        callback_url: str | None = None,
    ) -> TransactionRef:
        """Mock remittance — cross-border transfer."""
        await self.simulate_latency()
        tx_id = str(uuid4())
        logger.info(
            "🚀 Mock Remittance",
            amount=str(amount),
            currency=currency,
            from_phone=from_phone,
            to_phone=to_phone,
            reference=reference,
            transaction_id=tx_id,
        )
        return TransactionRef(
            transaction_id=tx_id,
            external_id=reference,
            status=TransactionStatus.SUCCESSFUL,
            created_at=datetime.utcnow(),
        )

    async def payment_request(
        self,
        amount: Decimal,
        phone: str,
        reference: str,
        callback_url: str | None = None,
    ) -> TransactionRef:
        """Mock payment — pass purchase or premium feature."""
        await self.simulate_latency()
        tx_id = str(uuid4())
        logger.info(
            "🚀 Mock Payment",
            amount=str(amount),
            phone=phone,
            reference=reference,
            transaction_id=tx_id,
        )
        return TransactionRef(
            transaction_id=tx_id,
            external_id=reference,
            status=TransactionStatus.SUCCESSFUL,
            created_at=datetime.utcnow(),
        )

    async def request_otp(self, phone: str) -> None:
        """Mock OTP request — always succeeds in demo mode."""
        await self.simulate_latency()
        logger.info("📱 Mock OTP sent", phone=phone)

    async def verify_otp(self, phone: str, otp: str) -> AuthToken:
        """Mock OTP verification — accepts any 4-digit code in demo mode."""
        await self.simulate_latency()
        logger.info("✅ Mock OTP verified", phone=phone)
        return AuthToken(
            access_token=f"mock_jwt_{uuid4().hex[:32]}",
            token_type="Bearer",
            expires_in=3600,
            created_at=datetime.utcnow(),
        )
