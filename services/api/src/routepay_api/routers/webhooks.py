"""Webhooks router — handle MoMo async notifications."""

from __future__ import annotations

import structlog
from fastapi import APIRouter, Request

logger = structlog.get_logger()

router = APIRouter(prefix="/webhooks", tags=["webhooks"])


@router.post("/momo")
async def momo_webhook(request: Request) -> dict:
    """Handle MoMo async callback notifications.

    In production, this would verify the callback signature,
    update the transaction status in the database, and
    notify connected WebSocket clients.
    """
    body = await request.json()
    logger.info("📨 MoMo webhook received", body=body)

    # In production:
    # 1. Verify callback signature
    # 2. Find transaction by momo_ref
    # 3. Update status
    # 4. Notify WebSocket clients
    # For now, just acknowledge receipt

    return {"status": "received", "message": "Webhook processed"}
