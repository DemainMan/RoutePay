"""MoMo SDK exception hierarchy."""

from __future__ import annotations


class MoMoError(Exception):
    """Base exception for all MoMo SDK errors."""


class MoMoAPIError(MoMoError):
    """Raised when the MoMo API returns a non-success response."""

    def __init__(self, status_code: int, message: str, response_body: str = "") -> None:
        self.status_code = status_code
        self.message = message
        self.response_body = response_body
        super().__init__(f"MoMo API error {status_code}: {message}")


class MoMoAuthError(MoMoError):
    """Raised when authentication with MoMo fails."""


class MoMoConnectionError(MoMoError):
    """Raised when unable to connect to the MoMo API."""


class MoMoTimeoutError(MoMoError):
    """Raised when a MoMo API request times out."""
