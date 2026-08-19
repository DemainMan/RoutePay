"""Trips router — start, complete, and list trips."""

from __future__ import annotations

from uuid import uuid4

from fastapi import APIRouter, HTTPException
from sqlalchemy import select

from routepay_api.deps import CurrentUser, DBSession
from routepay_api.models import (
    Route,
    Transaction,
    TransactionStatus,
    TransactionType,
    Trip,
    TripStatus,
    Vehicle,
)
from routepay_api.schemas import TripResponse, TripStartRequest

router = APIRouter(prefix="/trips", tags=["trips"])


@router.post("/start", response_model=TripResponse)
async def start_trip(
    body: TripStartRequest,
    user: CurrentUser,
    db: DBSession,
) -> TripResponse:
    """Start a new trip. The commuter scans a QR and confirms the fare."""
    vehicle = await db.get(Vehicle, body.vehicle_id)
    if vehicle is None:
        raise HTTPException(status_code=404, detail="Vehicle not found")

    route = await db.get(Route, body.route_id)
    if route is None:
        raise HTTPException(status_code=404, detail="Route not found")

    trip = Trip(
        commuter_id=user.id,
        vehicle_id=body.vehicle_id,
        route_id=body.route_id,
        fare_cents=route.fare_cents,
        status=TripStatus.IN_PROGRESS,
    )
    db.add(trip)
    await db.flush()
    await db.refresh(trip)

    return TripResponse(
        id=trip.id,
        commuter_id=trip.commuter_id,
        vehicle_id=trip.vehicle_id,
        route_id=trip.route_id,
        fare_cents=trip.fare_cents,
        fare_display=f"R{trip.fare_cents / 100:.2f}",
        status=trip.status,
        created_at=trip.created_at,
        route_name=route.name,
        vehicle_registration=vehicle.registration,
    )


@router.post("/{trip_id}/complete", response_model=TripResponse)
async def complete_trip(
    trip_id: int,
    user: CurrentUser,
    db: DBSession,
) -> TripResponse:
    """Complete a trip and trigger MoMo Collection for fare payment."""
    trip = await db.get(Trip, trip_id)
    if trip is None:
        raise HTTPException(status_code=404, detail="Trip not found")
    if trip.commuter_id != user.id:
        raise HTTPException(status_code=403, detail="Not your trip")
    if trip.status != TripStatus.IN_PROGRESS:
        raise HTTPException(status_code=400, detail="Trip is not in progress")

    trip.status = TripStatus.COMPLETED

    # Create a MoMo collection transaction
    tx = Transaction(
        trip_id=trip.id,
        momo_ref=f"momo_{uuid4().hex[:16]}",
        type=TransactionType.COLLECTION,
        amount_cents=trip.fare_cents,
        status=TransactionStatus.SUCCESSFUL,
    )
    db.add(tx)

    await db.flush()
    await db.refresh(trip)

    route = await db.get(Route, trip.route_id)
    vehicle = await db.get(Vehicle, trip.vehicle_id)

    return TripResponse(
        id=trip.id,
        commuter_id=trip.commuter_id,
        vehicle_id=trip.vehicle_id,
        route_id=trip.route_id,
        fare_cents=trip.fare_cents,
        fare_display=f"R{trip.fare_cents / 100:.2f}",
        status=trip.status,
        created_at=trip.created_at,
        route_name=route.name if route else "",
        vehicle_registration=vehicle.registration if vehicle else "",
    )


@router.get("/me", response_model=list[TripResponse])
async def my_trips(
    user: CurrentUser,
    db: DBSession,
) -> list[TripResponse]:
    """Get the current user's trip history."""
    result = await db.execute(
        select(Trip).where(Trip.commuter_id == user.id).order_by(Trip.created_at.desc())
    )
    trips = result.scalars().all()

    responses = []
    for t in trips:
        route = await db.get(Route, t.route_id)
        vehicle = await db.get(Vehicle, t.vehicle_id)
        responses.append(
            TripResponse(
                id=t.id,
                commuter_id=t.commuter_id,
                vehicle_id=t.vehicle_id,
                route_id=t.route_id,
                fare_cents=t.fare_cents,
                fare_display=f"R{t.fare_cents / 100:.2f}",
                status=t.status,
                created_at=t.created_at,
                route_name=route.name if route else "",
                vehicle_registration=vehicle.registration if vehicle else "",
            )
        )
    return responses
