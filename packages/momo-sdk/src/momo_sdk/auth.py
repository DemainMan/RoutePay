"""MoMo Auth — Account Verification (OTP-based login).

Handles OTP request and verification for phone-based authentication.
"""

from __future__ import annotations

import httpx
import structlog

from momo_sdk.exceptions import MoMoAPIError, MoMoAuthError
from momo_sdk.models import AuthToken

logger = structlog.get_logger()


class Auth:
    """MoMo Account Verification (OTP login).

    Provides methods to request and verify OTPs for phone-based auth.
    """

    def __init__(
        self,
        client: httpx.AsyncClient,
        subscription_key: str,
        base_url: str,
        is_mock: bool = False,
    ) -> None:
        self._client = client
        self._subscription_key = subscription_key
        self._base_url = base_url
        self._is_mock = is_mock

    async def request_otp(self, phone: str) -> None:
        """Send an OTP to the given phone number.

        Args:
            phone: Phone number in international format (e.g. +27821234567).

        Raises:
            MoMoAPIError: If the API returns a non-success status.
        """
        if self._is_mock:
            from momo_sdk.mock import MockMoMoBackend

            mock = MockMoMoBackend()
            await mock.request_otp(phone)
            return

        logger.info("📱 Requesting OTP", phone=phone)
        try:
            response = await self._client.post(
                f"{self._base_url}/requesttoken",
                json={"phoneNumber": phone},
                headers={
                    "Ocp-Apim-Subscription-Key": self._subscription_key,
                },
            )
            if response.status_code != 200:
                raise MoMoAPIError(
                    status_code=response.status_code,
                    message=f"OTP request failed: {response.text}",
                    response_body=response.text,
                )
            logger.info("✅ OTP sent successfully", phone=phone)
        except httpx.HTTPError as e:
            raise MoMoAuthError(f"Failed to connect to MoMo Auth: {e}") from e

    async def verify_otp(self, phone: str, otp: str) -> AuthToken:
        """Verify an OTP and return an auth token.

        Args:
            phone: Phone number in international format.
            otp: The OTP code received by the user.

        Returns:
            AuthToken with the access token.

        Raises:
            MoMoAPIError: If verification fails.
            MoMoAuthError: If the connection fails.
        """
        if self._is_mock:
            from momo_sdk.mock import MockMoMoBackend

            mock = MockMoMoBackend()
            return await mock.verify_otp(phone, otp)

        logger.info("🔐 Verifying OTP", phone=phone)
        try:
            response = await self._client.post(
                f"{self._base_url}/verification",
                json={"phoneNumber": phone, "code": otp},
                headers={
                    "Ocp-Apim-Subscription-Key": self._subscription_key,
                },
            )
            if response.status_code != 200:
                raise MoMoAPIError(
                    status_code=response.status_code,
                    message=f"OTP verification failed: {response.text}",
                    response_body=response.text,
                )
            data = response.json()
            token = AuthToken(
                access_token=data["token"],
                token_type=data.get("tokenType", "Bearer"),
                expires_in=data.get("expiresIn", 3600),
            )
            logger.info("✅ OTP verified, token issued", phone=phone)
            return token
        except httpx.HTTPError as e:
            raise MoMoAuthError(f"Failed to connect to MoMo Auth: {e}") from e
