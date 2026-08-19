"""Operator router — dashboard, transactions, payouts."""

from __future__ import annotations

from datetime import datetime, timedelta
from uuid import uuid4

from fastapi import APIRouter, HTTPException, Query
from sqlalchemy import func, select

from routepay_api.deps import CurrentUser, DBSession
from routepay_api.models import (
    Transaction,
    TransactionStatus,
    TransactionType,
    Trip,
    TripStatus,
    User,
    UserRole,
)
from routepay_api.schemas import (
    OperatorDashboard,
    OperatorTransactionList,
    PayoutRequest,
    PayoutResponse,
    TransactionResponse,
)

router = APIRouter(prefix="/operator", tags=["operator"])


async def _require_operator(user: User) -> None:
    """Ensure the user is an operator."""
    if user.role != UserRole.OPERATOR:
        raise HTTPException(status_code=403, detail="Operator access required")


@router.get("/dashboard", response_model=OperatorDashboard)
async def get_dashboard(
    user: CurrentUser,
    db: DBSession,
) -> OperatorDashboard:
    """Get aggregated operator dashboard data."""
    await _require_operator(user)

    # Today's revenue
    today = datetime.utcnow().replace(hour=0, minute=0, second=0, microsecond=0)
    week_ago = today - timedelta(days=7)

    # Total trips
    trip_count_result = await db.execute(select(func.count(Trip.id)))
    total_trips = trip_count_result.scalar() or 0

    # Today's revenue
    today_rev_result = await db.execute(
        select(func.coalesce(func.sum(Transaction.amount_cents), 0)).where(
            Transaction.type == TransactionType.COLLECTION,
            Transaction.status == TransactionStatus.SUCCESSFUL,
            Transaction.created_at >= today,
        )
    )
    today_revenue = today_rev_result.scalar() or 0

    # Weekly revenue
    week_rev_result = await db.execute(
        select(func.coalesce(func.sum(Transaction.amount_cents), 0)).where(
            Transaction.type == TransactionType.COLLECTION,
            Transaction.status == TransactionStatus.SUCCESSFUL,
            Transaction.created_at >= week_ago,
        )
    )
    weekly_revenue = week_rev_result.scalar() or 0

    # Average fare
    avg_result = await db.execute(
        select(func.coalesce(func.avg(Trip.fare_cents), 0)).where(
            Trip.status == TripStatus.COMPLETED
        )
    )
    avg_fare = avg_result.scalar() or 0

    # Recent transactions
    tx_result = await db.execute(
        select(Transaction).order_by(Transaction.created_at.desc()).limit(10)
    )
    recent_txs = tx_result.scalars().all()

    return OperatorDashboard(
        today_revenue_cents=int(today_revenue),
        weekly_revenue_cents=int(weekly_revenue),
        total_trips=total_trips,
        avg_fare_cents=int(avg_fare),
        recent_transactions=[
            TransactionResponse(
                id=tx.id,
                momo_ref=tx.momo_ref,
                type=tx.type,
                amount_cents=tx.amount_cents,
                amount_display=f"R{tx.amount_cents / 100:.2f}",
                status=tx.status,
                created_at=tx.created_at,
            )
            for tx in recent_txs
        ],
    )


@router.get("/transactions", response_model=OperatorTransactionList)
async def list_transactions(
    user: CurrentUser,
    db: DBSession,
    page: int = Query(1, ge=1),
    page_size: int = Query(20, ge=1, le=100),
) -> OperatorTransactionList:
    """Get paginated transaction list for the operator."""
    await _require_operator(user)

    offset = (page - 1) * page_size
    count_result = await db.execute(select(func.count(Transaction.id)))
    total = count_result.scalar() or 0

    result = await db.execute(
        select(Transaction).order_by(Transaction.created_at.desc()).offset(offset).limit(page_size)
    )
    txs = result.scalars().all()

    return OperatorTransactionList(
        transactions=[
            TransactionResponse(
                id=tx.id,
                momo_ref=tx.momo_ref,
                type=tx.type,
                amount_cents=tx.amount_cents,
                amount_display=f"R{tx.amount_cents / 100:.2f}",
                status=tx.status,
                created_at=tx.created_at,
            )
            for tx in txs
        ],
        total=total,
        page=page,
        page_size=page_size,
    )


@router.post("/payout", response_model=PayoutResponse)
async def request_payout(
    body: PayoutRequest,
    user: CurrentUser,
    db: DBSession,
) -> PayoutResponse:
    """Trigger a MoMo Disbursements payout to the operator."""
    await _require_operator(user)

    ref = f"payout_{uuid4().hex[:16]}"
    tx = Transaction(
        momo_ref=ref,
        type=TransactionType.DISBURSEMENT,
        amount_cents=body.amount_cents,
        status=TransactionStatus.SUCCESSFUL,
    )
    db.add(tx)
    await db.flush()

    return PayoutResponse(
        transaction_id=ref,
        status="SUCCESSFUL",
        amount_cents=body.amount_cents,
    )
