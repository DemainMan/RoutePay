"""Pydantic models for MoMo SDK request/response types."""

from __future__ import annotations

import enum
from datetime import datetime
from decimal import Decimal

from pydantic import BaseModel, Field


class TransactionStatus(str, enum.Enum):
    """Status of a MoMo transaction."""

    PENDING = "PENDING"
    SUCCESSFUL = "SUCCESSFUL"
    FAILED = "FAILED"
    REJECTED = "REJECTED"
    TIMEOUT = "TIMEOUT"
    UNKNOWN = "UNKNOWN"


class TransactionRef(BaseModel):
    """Reference returned by a successful MoMo API call."""

    transaction_id: str
    external_id: str = ""
    status: TransactionStatus = TransactionStatus.PENDING
    created_at: datetime = Field(default_factory=datetime.utcnow)


class AuthToken(BaseModel):
    """JWT token from MoMo Account Verification."""

    access_token: str
    token_type: str = "Bearer"
    expires_in: int = 3600
    created_at: datetime = Field(default_factory=datetime.utcnow)

    @property
    def is_expired(self) -> bool:
        """Check if the token has expired."""
        elapsed = (datetime.utcnow() - self.created_at).total_seconds()
        return elapsed >= self.expires_in


class CollectionRequest(BaseModel):
    """Request to initiate a collection (fare payment)."""

    amount: Decimal
    currency: str = "ZAR"
    phone: str
    reference: str
    callback_url: str | None = None


class DisbursementRequest(BaseModel):
    """Request to initiate a disbursement (payout to operator)."""

    amount: Decimal
    currency: str = "ZAR"
    phone: str
    reference: str
    callback_url: str | None = None


class RemittanceRequest(BaseModel):
    """Request to send a remittance (cross-border transfer)."""

    amount: Decimal
    currency: str = "ZAR"
    from_phone: str
    to_phone: str
    reference: str
    callback_url: str | None = None


class PaymentRequest(BaseModel):
    """Request to initiate a payment (pass purchase, premium feature)."""

    amount: Decimal
    currency: str = "ZAR"
    phone: str
    reference: str
    callback_url: str | None = None


class OTPRequest(BaseModel):
    """Request to send an OTP to a phone number."""

    phone: str


class OTPVerifyRequest(BaseModel):
    """Request to verify an OTP."""

    phone: str
    otp: str


class CallbackPayload(BaseModel):
    """Payload sent by MoMo via webhook/callback."""

    transaction_id: str
    status: TransactionStatus
    amount: Decimal | None = None
    currency: str | None = None
    external_id: str | None = None
    reason: str | None = None
