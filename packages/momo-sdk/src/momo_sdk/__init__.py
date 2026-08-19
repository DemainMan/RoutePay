"""MoMo SDK — Python client for MTN MoMo Open API.

Provides async clients for Collections, Disbursements, Remittances,
Payments, and Account Verification (Auth).

Usage:
    from momo_sdk import MoMoClient

    async with MoMoClient(env="sandbox") as client:
        ref = await client.collections.pay(
            amount=Decimal("15.00"),
            phone="+27821234567",
            reference="trip_123",
        )
"""

from momo_sdk.client import MoMoClient
from momo_sdk.exceptions import (
    MoMoAPIError,
    MoMoAuthError,
    MoMoConnectionError,
    MoMoTimeoutError,
)
from momo_sdk.models import (
    AuthToken,
    TransactionRef,
    TransactionStatus,
)

__version__ = "0.1.0"

__all__ = [
    "MoMoClient",
    "MoMoAPIError",
    "MoMoAuthError",
    "MoMoConnectionError",
    "MoMoTimeoutError",
    "AuthToken",
    "TransactionRef",
    "TransactionStatus",
]
