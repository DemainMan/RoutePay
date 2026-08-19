"""SQLAlchemy ORM models — RoutePay database schema.

All models use SQLAlchemy 2.0 Mapped[] type hints.
"""

from __future__ import annotations

import enum
from datetime import datetime

from sqlalchemy import DateTime, Enum, ForeignKey, Numeric, String, Text, func
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship
from sqlalchemy.types import JSON

from routepay_api.database import engine


class Base(DeclarativeBase):
    """Base class for all SQLAlchemy models."""


class UserRole(str, enum.Enum):
    """User role enum."""

    COMMUTER = "COMMUTER"
    OPERATOR = "OPERATOR"
    DRIVER = "DRIVER"


class TripStatus(str, enum.Enum):
    """Trip status enum."""

    PLANNED = "PLANNED"
    IN_PROGRESS = "IN_PROGRESS"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"


class TransactionType(str, enum.Enum):
    """Transaction type enum."""

    COLLECTION = "COLLECTION"
    DISBURSEMENT = "DISBURSEMENT"
    REMITTANCE = "REMITTANCE"
    PAYMENT = "PAYMENT"


class TransactionStatus(str, enum.Enum):
    """Transaction status enum."""

    PENDING = "PENDING"
    SUCCESSFUL = "SUCCESSFUL"
    FAILED = "FAILED"
    REJECTED = "REJECTED"
    TIMEOUT = "TIMEOUT"


class TransportMode(str, enum.Enum):
    """Transport mode enum."""

    TAXI = "TAXI"
    BUS = "BUS"
    TRAIN = "TRAIN"
    WALKING = "WALKING"
    E_HAILING = "E_HAILING"


class PassType(str, enum.Enum):
    """Pass type enum."""

    DAILY = "DAILY"
    WEEKLY = "WEEKLY"
    MONTHLY = "MONTHLY"


class User(Base):
    """User model — commuters, operators, and drivers."""

    __tablename__ = "users"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    phone: Mapped[str] = mapped_column(String(20), unique=True, index=True)
    name: Mapped[str] = mapped_column(String(100), default="")
    role: Mapped[UserRole] = mapped_column(Enum(UserRole), default=UserRole.COMMUTER)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=func.now())

    # Relationships
    operator: Mapped[Operator | None] = relationship("Operator", back_populates="user", uselist=False)
    trips: Mapped[list[Trip]] = relationship("Trip", back_populates="commuter")
    passes: Mapped[list[Pass]] = relationship("Pass", back_populates="commuter")


class Operator(Base):
    """Operator model — taxi/bus company owners."""

    __tablename__ = "operators"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), unique=True)
    company_name: Mapped[str] = mapped_column(String(200), default="")
    association: Mapped[str] = mapped_column(String(200), default="")
    vehicle_count: Mapped[int] = mapped_column(default=0)

    # Relationships
    user: Mapped[User] = relationship("User", back_populates="operator")
    vehicles: Mapped[list[Vehicle]] = relationship("Vehicle", back_populates="operator")


class Route(Base):
    """Route model — taxi, bus, or multi-modal routes."""

    __tablename__ = "routes"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(200))
    start_point: Mapped[str] = mapped_column(String(200))
    end_point: Mapped[str] = mapped_column(String(200))
    fare_cents: Mapped[int] = mapped_column(Numeric(10, 0))
    mode: Mapped[TransportMode] = mapped_column(Enum(TransportMode), default=TransportMode.TAXI)
    geometry: Mapped[dict | None] = mapped_column(JSON, nullable=True)

    # Relationships
    vehicles: Mapped[list[Vehicle]] = relationship("Vehicle", back_populates="route")
    trips: Mapped[list[Trip]] = relationship("Trip", back_populates="route")


class Vehicle(Base):
    """Vehicle model — registered taxis, buses, etc."""

    __tablename__ = "vehicles"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    operator_id: Mapped[int] = mapped_column(ForeignKey("operators.id"))
    registration: Mapped[str] = mapped_column(String(20), unique=True)
    route_id: Mapped[int | None] = mapped_column(ForeignKey("routes.id"), nullable=True)
    qr_code: Mapped[str | None] = mapped_column(Text, nullable=True)

    # Relationships
    operator: Mapped[Operator] = relationship("Operator", back_populates="vehicles")
    route: Mapped[Route | None] = relationship("Route", back_populates="vehicles")
    trips: Mapped[list[Trip]] = relationship("Trip", back_populates="vehicle")


class Trip(Base):
    """Trip model — a single journey by a commuter."""

    __tablename__ = "trips"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    commuter_id: Mapped[int] = mapped_column(ForeignKey("users.id"))
    vehicle_id: Mapped[int] = mapped_column(ForeignKey("vehicles.id"))
    route_id: Mapped[int] = mapped_column(ForeignKey("routes.id"))
    fare_cents: Mapped[int] = mapped_column(Numeric(10, 0))
    status: Mapped[TripStatus] = mapped_column(Enum(TripStatus), default=TripStatus.PLANNED)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=func.now())

    # Relationships
    commuter: Mapped[User] = relationship("User", back_populates="trips")
    vehicle: Mapped[Vehicle] = relationship("Vehicle", back_populates="trips")
    route: Mapped[Route] = relationship("Route", back_populates="trips")
    transaction: Mapped[Transaction | None] = relationship("Transaction", back_populates="trip", uselist=False)


class Transaction(Base):
    """Transaction model — MoMo payment records."""

    __tablename__ = "transactions"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    trip_id: Mapped[int | None] = mapped_column(ForeignKey("trips.id"), nullable=True)
    momo_ref: Mapped[str] = mapped_column(String(100), unique=True, index=True)
    type: Mapped[TransactionType] = mapped_column(Enum(TransactionType))
    amount_cents: Mapped[int] = mapped_column(Numeric(10, 0))
    status: Mapped[TransactionStatus] = mapped_column(Enum(TransactionStatus), default=TransactionStatus.PENDING)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=func.now())

    # Relationships
    trip: Mapped[Trip | None] = relationship("Trip", back_populates="transaction")


class Pass(Base):
    """Pass model — daily/weekly/monthly travel passes."""

    __tablename__ = "passes"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    commuter_id: Mapped[int] = mapped_column(ForeignKey("users.id"))
    route_id: Mapped[int | None] = mapped_column(ForeignKey("routes.id"), nullable=True)
    type: Mapped[PassType] = mapped_column(Enum(PassType))
    valid_until: Mapped[datetime] = mapped_column(DateTime(timezone=True))
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), server_default=func.now())

    # Relationships
    commuter: Mapped[User] = relationship("User", back_populates="passes")


async def create_tables() -> None:
    """Create all database tables (dev only — use Alembic in prod)."""
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
