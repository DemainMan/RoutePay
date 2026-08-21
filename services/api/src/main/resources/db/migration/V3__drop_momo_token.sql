-- V3: Drop unused momo_token column from users table
ALTER TABLE users DROP COLUMN IF EXISTS momo_token;
