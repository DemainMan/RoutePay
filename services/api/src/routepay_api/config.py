"""Application configuration via Pydantic Settings."""

from __future__ import annotations

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """RoutePay API configuration.

    All values are loaded from environment variables or .env file.
    """

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="ignore")

    # App
    APP_NAME: str = "RoutePay"
    APP_ENV: str = "development"
    DEBUG: bool = True
    SECRET_KEY: str = "change-me-to-a-random-string"

    # Database
    DATABASE_URL: str = "sqlite+aiosqlite:///./routepay.db"

    # MoMo API
    MOMO_ENV: str = "mock"
    MOMO_COLLECTIONS_URL: str = "https://sandbox.momodeveloper.mtn.com/collection/v1_0"
    MOMO_DISBURSEMENTS_URL: str = "https://sandbox.momodeveloper.mtn.com/disbursement/v1_0"
    MOMO_REMITTANCES_URL: str = "https://sandbox.momodeveloper.mtn.com/remittance/v1_0"
    MOMO_PAYMENTS_URL: str = "https://sandbox.momodeveloper.mtn.com/payment/v1_0"
    MOMO_SUBSCRIPTION_KEY: str = ""
    MOMO_API_USER: str = ""
    MOMO_API_KEY: str = ""
    MOMO_ENVIRONMENT: str = "sandbox"
    MOMO_CALLBACK_URL: str = ""

    # Auth / JWT
    JWT_SECRET_KEY: str = "change-me-to-a-random-string"
    JWT_ALGORITHM: str = "HS256"
    JWT_EXPIRATION_MINUTES: int = 60

    # CORS
    CORS_ORIGINS: list[str] = ["http://localhost:3000", "http://localhost:8081"]


settings = Settings()
