"""Pydantic v2 schemas for request/response validation."""

from __future__ import annotations

from datetime import datetime

from pydantic import BaseModel, Field

from routepay_api.models import (
    PassType,
    TransactionStatus,
    TransactionType,
    TransportMode,
    TripStatus,
    UserRole,
)

# ── Auth ─────────────────────────────────────────────────────────────────────


class OTPRequest(BaseModel):
    """Request to send an OTP."""

    phone: str = Field(..., examples=["+27821234567"])


class OTPVerify(BaseModel):
    """Request to verify an OTP."""

    phone: str
    otp: str = Field(..., min_length=4, max_length=6)


class TokenResponse(BaseModel):
    """JWT token response."""

    access_token: str
    token_type: str = "Bearer"
    user_id: int
    role: UserRole


# ── User ─────────────────────────────────────────────────────────────────────


class UserResponse(BaseModel):
    """User profile response."""

    id: int
    phone: str
    name: str
    role: UserRole
    created_at: datetime


# ── Route ────────────────────────────────────────────────────────────────────


class RouteResponse(BaseModel):
    """Route list/detail response."""

    id: int
    name: str
    start_point: str
    end_point: str
    fare_cents: int
    fare_display: str = ""
    mode: TransportMode
    geometry: dict | None = None


# ── Trip ─────────────────────────────────────────────────────────────────────


class TripStartRequest(BaseModel):
    """Request to start a trip."""

    vehicle_id: int
    route_id: int


class TripResponse(BaseModel):
    """Trip detail response."""

    id: int
    commuter_id: int
    vehicle_id: int
    route_id: int
    fare_cents: int
    fare_display: str = ""
    status: TripStatus
    created_at: datetime
    route_name: str = ""
    vehicle_registration: str = ""


# ── Transaction ──────────────────────────────────────────────────────────────


class TransactionResponse(BaseModel):
    """Transaction detail response."""

    id: int
    momo_ref: str
    type: TransactionType
    amount_cents: int
    amount_display: str = ""
    status: TransactionStatus
    created_at: datetime


# ── Pass ─────────────────────────────────────────────────────────────────────


class PassPurchaseRequest(BaseModel):
    """Request to purchase a pass."""

    route_id: int | None = None
    type: PassType


class PassResponse(BaseModel):
    """Pass detail response."""

    id: int
    commuter_id: int
    route_id: int | None
    type: PassType
    valid_until: datetime
    created_at: datetime
    is_active: bool = True


# ── Operator ─────────────────────────────────────────────────────────────────


class OperatorDashboard(BaseModel):
    """Aggregated operator dashboard data."""

    today_revenue_cents: int = 0
    weekly_revenue_cents: int = 0
    total_trips: int = 0
    avg_fare_cents: int = 0
    top_routes: list[dict] = []
    recent_transactions: list[TransactionResponse] = []


class OperatorTransactionList(BaseModel):
    """Paginated operator transactions."""

    transactions: list[TransactionResponse]
    total: int
    page: int
    page_size: int


class PayoutRequest(BaseModel):
    """Request to trigger a payout to operator."""

    amount_cents: int
    phone: str


class PayoutResponse(BaseModel):
    """Payout initiation response."""

    transaction_id: str
    status: str
    amount_cents: int
