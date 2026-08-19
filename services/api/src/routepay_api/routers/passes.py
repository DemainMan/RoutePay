"""Passes router — buy and list travel passes."""

from __future__ import annotations

from datetime import datetime, timedelta

from fastapi import APIRouter, HTTPException
from sqlalchemy import select

from routepay_api.deps import CurrentUser, DBSession
from routepay_api.models import Pass, PassType
from routepay_api.schemas import PassPurchaseRequest, PassResponse

router = APIRouter(prefix="/passes", tags=["passes"])

PASS_DURATIONS = {
    PassType.DAILY: timedelta(days=1),
    PassType.WEEKLY: timedelta(weeks=1),
    PassType.MONTHLY: timedelta(days=30),
}

PASS_PRICES_CENTS = {
    PassType.DAILY: 5000,
    PassType.WEEKLY: 25000,
    PassType.MONTHLY: 80000,
}


@router.post("", response_model=PassResponse)
async def buy_pass(
    body: PassPurchaseRequest,
    user: CurrentUser,
    db: DBSession,
) -> PassResponse:
    """Purchase a daily, weekly, or monthly travel pass."""
    duration = PASS_DURATIONS.get(body.type)
    if duration is None:
        raise HTTPException(status_code=400, detail="Invalid pass type")

    now = datetime.utcnow()
    pass_obj = Pass(
        commuter_id=user.id,
        route_id=body.route_id,
        type=body.type,
        valid_until=now + duration,
    )
    db.add(pass_obj)
    await db.flush()
    await db.refresh(pass_obj)

    return PassResponse(
        id=pass_obj.id,
        commuter_id=pass_obj.commuter_id,
        route_id=pass_obj.route_id,
        type=pass_obj.type,
        valid_until=pass_obj.valid_until,
        created_at=pass_obj.created_at,
        is_active=True,
    )


@router.get("/me", response_model=list[PassResponse])
async def my_passes(
    user: CurrentUser,
    db: DBSession,
) -> list[PassResponse]:
    """Get the current user's active passes."""
    result = await db.execute(
        select(Pass).where(Pass.commuter_id == user.id).order_by(Pass.created_at.desc())
    )
    passes = result.scalars().all()
    now = datetime.utcnow()

    return [
        PassResponse(
            id=p.id,
            commuter_id=p.commuter_id,
            route_id=p.route_id,
            type=p.type,
            valid_until=p.valid_until,
            created_at=p.created_at,
            is_active=p.valid_until > now,
        )
        for p in passes
    ]
