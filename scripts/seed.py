"""Seed script — populate the database with demo data.

Usage:
    uv run python scripts/seed.py
"""

from __future__ import annotations

import asyncio
import random
from datetime import datetime, timedelta

from routepay_api.database import async_session_factory, engine
from routepay_api.models import (
    Base,
    Operator,
    Pass,
    PassType,
    Route,
    Transaction,
    TransactionStatus,
    TransactionType,
    TransportMode,
    Trip,
    TripStatus,
    User,
    UserRole,
    Vehicle,
)
from sqlalchemy import select


async def seed() -> None:
    """Seed the database with demo data."""
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

    async with async_session_factory() as db:
        # Check if already seeded
        result = await db.execute(select(User).limit(1))
        if result.scalar_one_or_none() is not None:
            print("⚠️  Database already seeded. Skipping.")
            return

        print("🌱 Seeding database...")

        # ── Routes ────────────────────────────────────────────────────────
        routes_data = [
            ("Johannesburg CBD → Sandton", "Johannesburg CBD", "Sandton", 1800, TransportMode.TAXI),
            ("Soweto → Johannesburg CBD", "Soweto", "Johannesburg CBD", 1500, TransportMode.TAXI),
            ("Pretoria CBD → Johannesburg CBD", "Pretoria CBD", "Johannesburg CBD", 3500, TransportMode.BUS),
            ("Alexandra → Sandton", "Alexandra", "Sandton", 1200, TransportMode.TAXI),
            ("Randburg → Rosebank", "Randburg", "Rosebank", 2000, TransportMode.TAXI),
            ("Midrand → Johannesburg CBD", "Midrand", "Johannesburg CBD", 2800, TransportMode.BUS),
            ("Centurion → Pretoria CBD", "Centurion", "Pretoria CBD", 1500, TransportMode.TAXI),
            ("Benoni → Johannesburg CBD", "Benoni", "Johannesburg CBD", 4000, TransportMode.BUS),
            ("Kempton Park → Sandton", "Kempton Park", "Sandton", 2500, TransportMode.TAXI),
            ("Roodepoort → Johannesburg CBD", "Roodepoort", "Johannesburg CBD", 2200, TransportMode.TAXI),
        ]
        routes = []
        for name, start, end, fare, mode in routes_data:
            route = Route(name=name, start_point=start, end_point=end, fare_cents=fare, mode=mode)
            db.add(route)
            routes.append(route)
        await db.flush()
        print(f"  ✅ {len(routes)} routes created")

        # ── Operators ─────────────────────────────────────────────────────
        operators_data = [
            ("Thabo Mokoena", "Thabo's Taxi Service", "Gauteng Taxi Association", 5),
            ("Nomsa Dlamini", "Nomsa Transport", "Soweto Taxi Association", 8),
            ("Pieter van der Merwe", "Pretoria Bus Co", "Pretoria Transport Association", 12),
        ]
        operators = []
        for name, company, assoc, vc in operators_data:
            user = User(phone=f"+2782{random.randint(1000000, 9999999)}", name=name, role=UserRole.OPERATOR)
            db.add(user)
            await db.flush()
            op = Operator(user_id=user.id, company_name=company, association=assoc, vehicle_count=vc)
            db.add(op)
            operators.append(op)
        await db.flush()
        print(f"  ✅ {len(operators)} operators created")

        # ── Vehicles ──────────────────────────────────────────────────────
        registrations = [
            "GP 123-456", "GP 234-567", "GP 345-678", "GP 456-789", "GP 567-890",
            "GP 678-901", "GP 789-012", "GP 890-123", "GP 901-234", "GP 012-345",
            "GP 111-222", "GP 333-444", "GP 555-666", "GP 777-888", "GP 999-000",
            "GP 112-233", "GP 445-566", "GP 778-899", "GP 101-202", "GP 303-404",
        ]
        vehicles = []
        for i, reg in enumerate(registrations):
            op = operators[i % len(operators)]
            route = routes[i % len(routes)]
            vehicle = Vehicle(operator_id=op.id, registration=reg, route_id=route.id)
            db.add(vehicle)
            vehicles.append(vehicle)
        await db.flush()
        print(f"  ✅ {len(vehicles)} vehicles created")

        # ── Commuters ─────────────────────────────────────────────────────
        commuters = []
        for i in range(15):
            user = User(
                phone=f"+2782{random.randint(1000000, 9999999)}",
                name=f"Commuter {i + 1}",
                role=UserRole.COMMUTER,
            )
            db.add(user)
            commuters.append(user)
        await db.flush()
        print(f"  ✅ {len(commuters)} commuters created")

        # ── Trips ─────────────────────────────────────────────────────────
        trips = []
        now = datetime.utcnow()
        for _i in range(50):
            commuter = random.choice(commuters)
            vehicle = random.choice(vehicles)
            route = vehicle.route or random.choice(routes)
            days_ago = random.randint(0, 30)
            created = now - timedelta(days=days_ago, hours=random.randint(0, 23))

            trip = Trip(
                commuter_id=commuter.id,
                vehicle_id=vehicle.id,
                route_id=route.id,
                fare_cents=route.fare_cents,
                status=TripStatus.COMPLETED,
                created_at=created,
            )
            db.add(trip)
            trips.append(trip)
        await db.flush()
        print(f"  ✅ {len(trips)} trips created")

        # ── Transactions ──────────────────────────────────────────────────
        for trip in trips:
            tx = Transaction(
                trip_id=trip.id,
                momo_ref=f"momo_{trip.id:06d}",
                type=TransactionType.COLLECTION,
                amount_cents=trip.fare_cents,
                status=TransactionStatus.SUCCESSFUL,
                created_at=trip.created_at,
            )
            db.add(tx)
        print(f"  ✅ {len(trips)} transactions created")

        # ── Passes ────────────────────────────────────────────────────────
        for commuter in commuters[:5]:
            pass_obj = Pass(
                commuter_id=commuter.id,
                type=PassType.WEEKLY,
                valid_until=now + timedelta(days=7),
            )
            db.add(pass_obj)
        print("  ✅ 5 passes created")

        await db.commit()
        print("🎉 Database seeded successfully!")


if __name__ == "__main__":
    asyncio.run(seed())
