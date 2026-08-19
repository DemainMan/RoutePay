"""Auth router — OTP request/verify, JWT login."""

from __future__ import annotations

from typing import Annotated

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from routepay_api.database import get_db
from routepay_api.deps import create_access_token
from routepay_api.models import User, UserRole
from routepay_api.schemas import OTPRequest, OTPVerify, TokenResponse

router = APIRouter(prefix="/auth", tags=["auth"])

# In-memory OTP store for mock mode (phone -> otp)
_otp_store: dict[str, str] = {}


@router.post("/request-otp", response_model=dict)
async def request_otp(body: OTPRequest) -> dict:
    """Send an OTP to the given phone number.

    In mock mode, any 4-digit code will be accepted during verification.
    """
    # For mock mode, store a default OTP
    _otp_store[body.phone] = "1234"
    return {"message": f"OTP sent to {body.phone}"}


@router.post("/verify-otp", response_model=TokenResponse)
async def verify_otp(
    body: OTPVerify,
    db: Annotated[AsyncSession, Depends(get_db)],
) -> TokenResponse:
    """Verify an OTP and return a JWT token.

    In mock mode, accepts any 4-digit code.
    """
    # In mock mode, accept any 4-digit code
    if len(body.otp) != 4:
        raise HTTPException(status_code=400, detail="OTP must be 4 digits")

    # Find or create user
    result = await db.execute(select(User).where(User.phone == body.phone))
    user = result.scalar_one_or_none()

    if user is None:
        # Auto-register as commuter
        user = User(phone=body.phone, name="", role=UserRole.COMMUTER)
        db.add(user)
        await db.flush()
        await db.refresh(user)

    token = create_access_token(user.id, user.role.value)
    return TokenResponse(
        access_token=token,
        user_id=user.id,
        role=user.role,
    )
