"""MoMo Collections API — fare payments from commuters.

Handles the Collections API flow:
1. Request to Pay — initiate the payment
2. Get Transaction Status — check if payment succeeded
"""

from __future__ import annotations

from decimal import Decimal

import httpx
import structlog

from momo_sdk.exceptions import MoMoAPIError, MoMoConnectionError
from momo_sdk.models import TransactionRef, TransactionStatus

logger = structlog.get_logger()


class Collections:
    """MoMo Collections API client.

    Handles fare payments from commuters via QR code or manual entry.
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

    async def pay(
        self,
        amount: Decimal,
        phone: str,
        reference: str,
        callback_url: str | None = None,
    ) -> TransactionRef:
        """Initiate a fare payment (Request to Pay).

        Args:
            amount: Fare amount in ZAR (use Decimal for precision).
            phone: Commuter's phone number (international format).
            reference: Trip/transaction reference for reconciliation.
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
            return await mock.collection_pay(amount, phone, reference, callback_url)

        effective_callback = callback_url or self._callback_url
        payload = {
            "amount": str(amount),
            "currency": "ZAR",
            "externalId": reference,
            "payer": {"partyIdType": "MSISDN", "partyId": phone},
            "payerMessage": f"RoutePay fare: R{amount}",
            "payeeNote": f"Trip reference: {reference}",
        }
        if effective_callback:
            payload["callbackUrl"] = effective_callback

        logger.info(
            "💰 Initiating collection",
            amount=str(amount),
            phone=phone,
            reference=reference,
        )

        try:
            response = await self._client.post(
                f"{self._base_url}/requesttopay",
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
                    message=f"Collection failed: {response.text}",
                    response_body=response.text,
                )
            status = TransactionStatus.PENDING if response.status_code == 202 else TransactionStatus.SUCCESSFUL
            logger.info("✅ Collection initiated", reference=reference, status=status.value)
            return TransactionRef(
                transaction_id=reference if isinstance(reference, type(response.json().get("transactionId", ""))) else response.json().get("transactionId", reference),
                external_id=reference,
                status=status,
            )
        except httpx.HTTPError as e:
            raise MoMoConnectionError(f"Failed to connect to MoMo Collections: {e}") from e

    async def get_status(self, transaction_id: str) -> TransactionRef:
        """Check the status of a collection transaction.

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
                f"{self._base_url}/requesttopay/{transaction_id}",
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
