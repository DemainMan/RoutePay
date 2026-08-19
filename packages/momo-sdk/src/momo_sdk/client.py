"""MoMo Client — main entry point for the SDK.

Usage:
    async with MoMoClient(env="sandbox") as client:
        ref = await client.collections.pay(
            amount=Decimal("15.00"),
            phone="+27821234567",
            reference="trip_123",
        )
"""

from __future__ import annotations

import httpx
import structlog

from momo_sdk.auth import Auth
from momo_sdk.collections import Collections
from momo_sdk.disbursements import Disbursements
from momo_sdk.payments import Payments
from momo_sdk.remittances import Remittances

logger = structlog.get_logger()


class MoMoClient:
    """Unified MoMo API client.

    Provides access to all five MoMo API modules:
    - Collections (fare payments)
    - Disbursements (operator payouts)
    - Remittances (cross-border transfers)
    - Payments (passes, premium features)
    - Auth (OTP-based login)

    Toggle between mock and live via `env` parameter or MOMO_ENV env var.
    """

    def __init__(
        self,
        env: str = "mock",
        subscription_key: str = "",
        api_user: str = "",
        api_key: str = "",
        collections_url: str = "https://sandbox.momodeveloper.mtn.com/collection/v1_0",
        disbursements_url: str = "https://sandbox.momodeveloper.mtn.com/disbursement/v1_0",
        remittances_url: str = "https://sandbox.momodeveloper.mtn.com/remittance/v1_0",
        payments_url: str = "https://sandbox.momodeveloper.mtn.com/payment/v1_0",
        auth_url: str = "https://sandbox.momodeveloper.mtn.com/collection/token/",
        callback_url: str | None = None,
    ) -> None:
        self._env = env
        self._is_mock = env == "mock"
        self._callback_url = callback_url

        self._http_client = httpx.AsyncClient(timeout=30.0)

        effective_auth_url = auth_url if not self._is_mock else ""

        self.collections = Collections(
            client=self._http_client,
            subscription_key=subscription_key,
            base_url=collections_url,
            callback_url=callback_url,
            is_mock=self._is_mock,
        )
        self.disbursements = Disbursements(
            client=self._http_client,
            subscription_key=subscription_key,
            base_url=disbursements_url,
            callback_url=callback_url,
            is_mock=self._is_mock,
        )
        self.remittances = Remittances(
            client=self._http_client,
            subscription_key=subscription_key,
            base_url=remittances_url,
            callback_url=callback_url,
            is_mock=self._is_mock,
        )
        self.payments = Payments(
            client=self._http_client,
            subscription_key=subscription_key,
            base_url=payments_url,
            callback_url=callback_url,
            is_mock=self._is_mock,
        )
        self.auth = Auth(
            client=self._http_client,
            subscription_key=subscription_key,
            base_url=effective_auth_url,
            is_mock=self._is_mock,
        )

        mode_label = "🧪 MOCK" if self._is_mock else "🔴 LIVE"
        logger.info("🔌 MoMoClient initialized", env=env, mode=mode_label)

    async def __aenter__(self) -> MoMoClient:
        """Async context manager entry."""
        return self

    async def __aexit__(
        self,
        exc_type: type[BaseException] | None,
        exc_val: BaseException | None,
        exc_tb: object,
    ) -> None:
        """Async context manager exit — close HTTP client."""
        await self.close()

    async def close(self) -> None:
        """Close the underlying HTTP client."""
        await self._http_client.aclose()
