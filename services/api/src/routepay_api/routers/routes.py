"""Routes router — list and search available routes."""

from __future__ import annotations

from typing import Annotated

from fastapi import APIRouter, Depends, Query
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from routepay_api.database import get_db
from routepay_api.models import Route
from routepay_api.schemas import RouteResponse

router = APIRouter(prefix="/routes", tags=["routes"])


@router.get("", response_model=list[RouteResponse])
async def list_routes(
    db: Annotated[AsyncSession, Depends(get_db)],
    start: str | None = Query(None, description="Filter by start point"),
    end: str | None = Query(None, description="Filter by end point"),
) -> list[RouteResponse]:
    """List all available routes with optional filtering."""
    query = select(Route)
    if start:
        query = query.where(Route.start_point.ilike(f"%{start}%"))
    if end:
        query = query.where(Route.end_point.ilike(f"%{end}%"))

    result = await db.execute(query)
    routes = result.scalars().all()

    return [
        RouteResponse(
            id=r.id,
            name=r.name,
            start_point=r.start_point,
            end_point=r.end_point,
            fare_cents=r.fare_cents,
            fare_display=f"R{r.fare_cents / 100:.2f}",
            mode=r.mode,
            geometry=r.geometry,
        )
        for r in routes
    ]


@router.get("/{route_id}", response_model=RouteResponse)
async def get_route(
    route_id: int,
    db: Annotated[AsyncSession, Depends(get_db)],
) -> RouteResponse:
    """Get a single route by ID."""
    result = await db.execute(select(Route).where(Route.id == route_id))
    route = result.scalar_one_or_none()
    if route is None:
        from fastapi import HTTPException

        raise HTTPException(status_code=404, detail="Route not found")

    return RouteResponse(
        id=route.id,
        name=route.name,
        start_point=route.start_point,
        end_point=route.end_point,
        fare_cents=route.fare_cents,
        fare_display=f"R{route.fare_cents / 100:.2f}",
        mode=route.mode,
        geometry=route.geometry,
    )
