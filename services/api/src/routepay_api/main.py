"""RoutePay API — FastAPI application entry point."""

from __future__ import annotations

from collections.abc import AsyncGenerator
from contextlib import asynccontextmanager

import structlog
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from routepay_api.config import settings
from routepay_api.models import create_tables
from routepay_api.routers import auth, operator, passes, routes, trips, webhooks
from routepay_api.websocket import operator_ws

logger = structlog.get_logger()


@asynccontextmanager
async def lifespan(app: FastAPI) -> AsyncGenerator[None, None]:
    """Application lifespan — create tables on startup."""
    logger.info("🚀 Starting RoutePay API", env=settings.APP_ENV)
    await create_tables()
    logger.info("✅ Database tables created")
    yield
    logger.info("👋 Shutting down RoutePay API")


app = FastAPI(
    title="RoutePay API",
    description="MoMo-powered Mini App for cashless commuter payments in South Africa",
    version="0.1.0",
    lifespan=lifespan,
    docs_url="/docs",
    redoc_url="/redoc",
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Routers
app.include_router(auth.router, prefix="/api/v1")
app.include_router(routes.router, prefix="/api/v1")
app.include_router(trips.router, prefix="/api/v1")
app.include_router(passes.router, prefix="/api/v1")
app.include_router(operator.router, prefix="/api/v1")
app.include_router(webhooks.router, prefix="/api/v1")

# WebSocket
app.websocket("/ws/operator/{operator_id}")(operator_ws)


@app.get("/")
async def root() -> dict[str, str]:
    """Health check endpoint."""
    return {"app": "RoutePay", "status": "running", "version": "0.1.0"}


@app.get("/health")
async def health() -> dict[str, str]:
    """Health check endpoint."""
    return {"status": "healthy"}
