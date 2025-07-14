#!/bin/bash

# Capstone Management System - Deployment Script
# This script deploys the application using Docker Compose

set -e  # Exit on any error

echo "🚀 Starting Capstone Management System Deployment..."

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "❌ docker-compose is not installed. Please install it and try again."
    exit 1
fi

# Check if .env file exists
if [ ! -f .env ]; then
    echo "⚠️ .env file not found. Creating from template..."
    cp .env.production .env
    echo "📝 Please edit .env file with your production values before continuing."
    echo "Press Enter to continue after editing .env file..."
    read -r
fi

# Create logs directory if it doesn't exist
mkdir -p logs

echo "🔨 Building and starting services..."

# Build and start services
docker-compose -f docker-compose.yml down --remove-orphans
docker-compose -f docker-compose.yml build --no-cache
docker-compose -f docker-compose.yml up -d

echo "⏳ Waiting for services to start..."

# Wait for database to be ready
echo "Waiting for database..."
timeout 60 bash -c 'until docker-compose exec database pg_isready -h localhost -p 5432; do sleep 2; done'

# Wait for backend to be healthy
echo "Waiting for backend..."
timeout 120 bash -c 'until curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; do sleep 5; done'

# Wait for frontend to be ready
echo "Waiting for frontend..."
timeout 60 bash -c 'until curl -f http://localhost/health >/dev/null 2>&1; do sleep 2; done'

echo "✅ Deployment completed successfully!"
echo ""
echo "🌐 Application is available at:"
echo "   Frontend: http://localhost"
echo "   Backend API: http://localhost:8080/api"
echo "   Health Check: http://localhost:8080/actuator/health"
echo ""
echo "📊 To view logs:"
echo "   docker-compose logs -f"
echo ""
echo "🛑 To stop the application:"
echo "   docker-compose down"
echo ""

# Show running containers
echo "📋 Running containers:"
docker-compose ps

echo ""
echo "🎉 Deployment successful! The Capstone Management System is now running."

# Display test credentials
echo ""
echo "🔑 Test Credentials (check TEST_CREDENTIALS.md for full list):"
echo "   Admin: admin/admin123"
echo "   Manager: m.johnson/manager123"
echo "   Employee: j.smith/employee123"
