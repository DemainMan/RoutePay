"""WebSocket handler for real-time operator dashboard updates."""

from __future__ import annotations

import json
from collections import defaultdict
from typing import Any

from fastapi import WebSocket, WebSocketDisconnect


class ConnectionManager:
    """Manages WebSocket connections for real-time updates."""

    def __init__(self) -> None:
        self._connections: dict[int, list[WebSocket]] = defaultdict(list)

    async def connect(self, websocket: WebSocket, operator_id: int) -> None:
        """Accept and register a new WebSocket connection."""
        await websocket.accept()
        self._connections[operator_id].append(websocket)

    def disconnect(self, websocket: WebSocket, operator_id: int) -> None:
        """Remove a WebSocket connection."""
        if websocket in self._connections[operator_id]:
            self._connections[operator_id].remove(websocket)

    async def broadcast_to_operator(self, operator_id: int, data: dict[str, Any]) -> None:
        """Send a message to all connections for a specific operator."""
        disconnected: list[WebSocket] = []
        for ws in self._connections.get(operator_id, []):
            try:
                await ws.send_text(json.dumps(data, default=str))
            except Exception:
                disconnected.append(ws)
        for ws in disconnected:
            self.disconnect(ws, operator_id)


manager = ConnectionManager()


async def operator_ws(websocket: WebSocket, operator_id: int) -> None:
    """WebSocket endpoint for real-time operator dashboard updates."""
    await manager.connect(websocket, operator_id)
    try:
        while True:
            # Keep connection alive; client may send pings
            data = await websocket.receive_text()
            if data == "ping":
                await websocket.send_text("pong")
    except WebSocketDisconnect:
        manager.disconnect(websocket, operator_id)
