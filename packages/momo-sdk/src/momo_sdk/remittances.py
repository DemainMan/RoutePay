"""MoMo Remittances API — cross-border migrant corridor transfers.

Handles the Remittances API flow:
1. Send — initiate a cross-border transfer
2. Get Transaction Status — check if transfer succeeded
"""

from __future__ import annotations

from decimal import Decimal

import httpx
import structlog

from momo_sdk.exceptions import MoMoAPIError, MoMoConnectionError
from momo_sdk.models import TransactionRef, TransactionStatus

logger = structlog.get_logger()


class Remittances:
    """MoMo Remittances API client.

    Handles cross-border transfers for migrant worker corridors
    (e.g. South Africa → Zimbabwe, Mozambique, Lesotho).
    """

    def __init__(
        self,
        client: httpx.AsyncClient,
        subscription_key: str,
        base_url: str,
        callback_url: str | None = None,
        is_mock: bool = False,
    ) -> None:
        self._client = client
        self._subscription_key = subscription_key
        self._base_url = base_url
        self._callback_url = callback_url
        self._is_mock = is_mock

    async def send(
        self,
        amount: Decimal,
        from_phone: str,
        to_phone: str,
        reference: str,
        currency: str = "ZAR",
        callback_url: str | None = None,
    ) -> TransactionRef:
        """Send a cross-border remittance.

        Args:
            amount: Transfer amount.
            from_phone: Sender's phone number (international format).
            to_phone: Recipient's phone number (international format).
            reference: Transfer reference for reconciliation.
            currency: Currency code (default ZAR).
            callback_url: Optional override for the default callback URL.

        Returns:
            TransactionRef with the MoMo transaction ID and initial status.

        Raises:
            MoMoAPIError: If the API returns an error.
            MoMoConnectionError: If unable to reach the MoMo API.
        """
        if self._is_mock:
            from momo_sdk.mock import MockMoMoBackend

            mock = MockMoMoBackend()
            return await mock.remittance_send(
                amount, currency, from_phone, to_phone, reference, callback_url
            )

        effective_callback = callback_url or self._callback_url
        payload = {
            "amount": str(amount),
            "currency": currency,
            "externalId": reference,
            "sender": {"partyIdType": "MSISDN", "partyId": from_phone},
            "receiver": {"partyIdType": "MSISDN", "partyId": to_phone},
        }
        if effective_callback:
            payload["callbackUrl"] = effective_callback

        logger.info(
            "🌍 Initiating remittance",
            amount=str(amount),
            currency=currency,
            from_phone=from_phone,
            to_phone=to_phone,
            reference=reference,
        )

        try:
            response = await self._client.post(
                f"{self._base_url}/transfer",
                json=payload,
                headers={
                    "X-Reference-Id": reference,
                    "X-Target-Environment": "sandbox",
                    "Ocp-Apim-Subscription-Key": self._subscription_key,
                    "Content-Type": "application/json",
                },
            )
            if response.status_code not in (200, 202):
                raise MoMoAPIError(
                    status_code=response.status_code,
                    message=f"Remittance failed: {response.text}",
                    response_body=response.text,
                )
            status = TransactionStatus.PENDING if response.status_code == 202 else TransactionStatus.SUCCESSFUL
            logger.info("✅ Remittance initiated", reference=reference, status=status.value)
            return TransactionRef(
                transaction_id=reference,
                external_id=reference,
                status=status,
            )
        except httpx.HTTPError as e:
            raise MoMoConnectionError(f"Failed to connect to MoMo Remittances: {e}") from e

    async def get_status(self, transaction_id: str) -> TransactionRef:
        """Check the status of a remittance transaction.

        Args:
            transaction_id: The MoMo transaction ID.

        Returns:
            TransactionRef with the current status.
        """
        if self._is_mock:
            return TransactionRef(
                transaction_id=transaction_id,
                external_id=transaction_id,
                status=TransactionStatus.SUCCESSFUL,
            )

        try:
            response = await self._client.get(
                f"{self._base_url}/transfer/{transaction_id}",
                headers={
                    "X-Target-Environment": "sandbox",
                    "Ocp-Apim-Subscription-Key": self._subscription_key,
                },
            )
            if response.status_code != 200:
                raise MoMoAPIError(
                    status_code=response.status_code,
                    message=f"Status check failed: {response.text}",
                )
            data = response.json()
            status_map = {
                "SUCCESSFUL": TransactionStatus.SUCCESSFUL,
                "FAILED": TransactionStatus.FAILED,
                "REJECTED": TransactionStatus.REJECTED,
                "TIMEOUT": TransactionStatus.TIMEOUT,
            }
            return TransactionRef(
                transaction_id=transaction_id,
                external_id=data.get("externalId", ""),
                status=status_map.get(data.get("status", ""), TransactionStatus.UNKNOWN),
            )
        except httpx.HTTPError as e:
            raise MoMoConnectionError(f"Failed to check transaction status: {e}") from e
