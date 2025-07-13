-- Initialize database for Docker deployment
-- This script runs when the PostgreSQL container starts for the first time

-- Create database (if not exists)
SELECT 'CREATE DATABASE capstone_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'capstone_db');

-- Connect to the database
\c capstone_db;

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE capstone_db TO capstone_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO capstone_user;

-- Set timezone
SET timezone = 'UTC';

-- Optional: Create initial admin user (password will be encoded by Spring Boot)
-- This is handled by the application's DataSeeder component

-- Performance optimizations
-- Increase shared_buffers for better performance
-- ALTER SYSTEM SET shared_buffers = '256MB';
-- ALTER SYSTEM SET effective_cache_size = '1GB';
-- ALTER SYSTEM SET random_page_cost = 1.1;

-- Logging configuration for monitoring
ALTER SYSTEM SET log_statement = 'mod';
ALTER SYSTEM SET log_min_duration_statement = 1000;

-- Reload configuration
SELECT pg_reload_conf();

-- Create a health check function
CREATE OR REPLACE FUNCTION health_check()
RETURNS TEXT AS $$
BEGIN
    RETURN 'Database is healthy at ' || NOW();
END;
$$ LANGUAGE plpgsql;

-- Grant execute permission on health check function
GRANT EXECUTE ON FUNCTION health_check() TO capstone_user;
