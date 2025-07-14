@echo off
REM Capstone Management System - Deployment Script for Windows
REM This script deploys the application using Docker Compose

echo 🚀 Starting Capstone Management System Deployment...

REM Check if Docker is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker is not running. Please start Docker and try again.
    exit /b 1
)

REM Check if docker-compose is available
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ docker-compose is not installed. Please install it and try again.
    exit /b 1
)

REM Check if .env file exists
if not exist .env (
    echo ⚠️ .env file not found. Creating from template...
    copy .env.production .env
    echo 📝 Please edit .env file with your production values before continuing.
    echo Press Enter to continue after editing .env file...
    pause >nul
)

REM Create logs directory if it doesn't exist
if not exist logs mkdir logs

echo 🔨 Building and starting services...

REM Build and start services
docker-compose -f docker-compose.yml down --remove-orphans
docker-compose -f docker-compose.yml build --no-cache
docker-compose -f docker-compose.yml up -d

echo ⏳ Waiting for services to start...

REM Wait for services (simplified for Windows)
echo Waiting for database...
timeout /t 30 /nobreak >nul

echo Waiting for backend...
timeout /t 60 /nobreak >nul

echo Waiting for frontend...
timeout /t 30 /nobreak >nul

echo ✅ Deployment completed successfully!
echo.
echo 🌐 Application is available at:
echo    Frontend: http://localhost
echo    Backend API: http://localhost:8080/api
echo    Health Check: http://localhost:8080/actuator/health
echo.
echo 📊 To view logs:
echo    docker-compose logs -f
echo.
echo 🛑 To stop the application:
echo    docker-compose down
echo.

REM Show running containers
echo 📋 Running containers:
docker-compose ps

echo.
echo 🎉 Deployment successful! The Capstone Management System is now running.

REM Display test credentials
echo.
echo 🔑 Test Credentials (check TEST_CREDENTIALS.md for full list):
echo    Admin: admin/admin123
echo    Manager: m.johnson/manager123
echo    Employee: j.smith/employee123

pause
