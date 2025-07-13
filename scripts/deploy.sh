#!/bin/bash

# Capstone Management System - Deployment Script
# This script handles the deployment of the application using Docker

set -e

# Configuration
COMPOSE_FILE="docker-compose.yml"
PROD_COMPOSE_FILE="docker-compose.prod.yml"
ENV_FILE=".env.local"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    log_step "Checking prerequisites..."
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed"
        exit 1
    fi
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose is not installed"
        exit 1
    fi
    
    # Check environment file
    if [ ! -f "$ENV_FILE" ]; then
        log_warn "Environment file $ENV_FILE not found"
        if [ -f ".env" ]; then
            log_info "Copying .env to $ENV_FILE"
            cp .env "$ENV_FILE"
        else
            log_error "No environment file found"
            exit 1
        fi
    fi
    
    log_info "Prerequisites check passed"
}

# Build frontend
build_frontend() {
    log_step "Building frontend..."
    
    cd src/main/capstone-ui
    
    # Install dependencies
    if [ -f "package-lock.json" ]; then
        npm ci
    else
        npm install
    fi
    
    # Build for production
    npm run build
    
    cd ../../..
    log_info "Frontend build completed"
}

# Development deployment
deploy_dev() {
    log_step "Deploying for development..."
    
    # Stop existing containers
    docker-compose down --remove-orphans
    
    # Build and start services
    docker-compose up --build -d
    
    # Wait for services to be ready
    wait_for_services
    
    log_info "Development deployment completed"
    show_access_info "development"
}

# Production deployment
deploy_prod() {
    log_step "Deploying for production..."
    
    # Validate production environment
    validate_prod_env
    
    # Stop existing containers
    docker-compose -f "$PROD_COMPOSE_FILE" down --remove-orphans
    
    # Pull latest images
    docker-compose -f "$PROD_COMPOSE_FILE" pull
    
    # Build and start services
    docker-compose -f "$PROD_COMPOSE_FILE" up --build -d
    
    # Wait for services to be ready
    wait_for_services "$PROD_COMPOSE_FILE"
    
    log_info "Production deployment completed"
    show_access_info "production"
}

# Validate production environment
validate_prod_env() {
    log_step "Validating production environment..."
    
    # Check for default passwords
    if grep -q "CHANGE_THIS" "$ENV_FILE"; then
        log_error "Default passwords found in $ENV_FILE. Please update all CHANGE_THIS values."
        exit 1
    fi
    
    # Check JWT secret length
    JWT_SECRET=$(grep "^JWT_SECRET=" "$ENV_FILE" | cut -d'=' -f2)
    if [ ${#JWT_SECRET} -lt 32 ]; then
        log_error "JWT_SECRET must be at least 32 characters long"
        exit 1
    fi
    
    log_info "Production environment validation passed"
}

# Wait for services to be ready
wait_for_services() {
    local compose_file=${1:-$COMPOSE_FILE}
    
    log_step "Waiting for services to be ready..."
    
    # Wait for backend health check
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if docker-compose -f "$compose_file" exec -T backend curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
            log_info "Backend is ready"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            log_error "Backend failed to start within $max_attempts attempts"
            show_logs "$compose_file"
            exit 1
        fi
        
        log_info "Waiting for backend... (attempt $attempt/$max_attempts)"
        sleep 10
        ((attempt++))
    done
    
    # Wait for frontend
    sleep 5
    if docker-compose -f "$compose_file" exec -T frontend curl -f http://localhost/health > /dev/null 2>&1; then
        log_info "Frontend is ready"
    else
        log_warn "Frontend health check failed, but continuing..."
    fi
}

# Show access information
show_access_info() {
    local env=$1
    
    echo ""
    echo "========================================"
    echo "  Deployment Complete - $env"
    echo "========================================"
    
    if [ "$env" = "development" ]; then
        echo "Frontend:    http://localhost"
        echo "Backend API: http://localhost:8080/api"
        echo "Database:    localhost:5432"
        echo "Redis:       localhost:6379"
    else
        echo "Frontend:    https://your-domain.com"
        echo "Backend API: https://your-domain.com/api"
        echo "Database:    Internal only"
        echo "Redis:       Internal only"
    fi
    
    echo ""
    echo "Default Admin Credentials:"
    echo "Username: admin"
    echo "Password: admin123"
    echo ""
    echo "To view logs: docker-compose logs -f"
    echo "To stop:      docker-compose down"
    echo "========================================"
}

# Show service logs
show_logs() {
    local compose_file=${1:-$COMPOSE_FILE}
    
    log_error "Showing recent logs for debugging:"
    docker-compose -f "$compose_file" logs --tail=50
}

# Update deployment
update() {
    local env=${1:-development}
    
    log_step "Updating $env deployment..."
    
    # Pull latest code (if in git repo)
    if [ -d ".git" ]; then
        log_info "Pulling latest code..."
        git pull
    fi
    
    # Choose appropriate deployment
    if [ "$env" = "production" ]; then
        deploy_prod
    else
        deploy_dev
    fi
}

# Stop deployment
stop() {
    local env=${1:-development}
    
    log_step "Stopping $env deployment..."
    
    if [ "$env" = "production" ]; then
        docker-compose -f "$PROD_COMPOSE_FILE" down
    else
        docker-compose down
    fi
    
    log_info "Deployment stopped"
}

# Show status
status() {
    log_step "Showing deployment status..."
    
    echo "Development services:"
    docker-compose ps
    
    echo ""
    echo "Production services:"
    docker-compose -f "$PROD_COMPOSE_FILE" ps 2>/dev/null || echo "Production not running"
    
    echo ""
    echo "Docker system info:"
    docker system df
}

# Cleanup
cleanup() {
    log_step "Cleaning up Docker resources..."
    
    # Stop all services
    docker-compose down --remove-orphans 2>/dev/null || true
    docker-compose -f "$PROD_COMPOSE_FILE" down --remove-orphans 2>/dev/null || true
    
    # Remove unused resources
    docker system prune -f
    
    log_info "Cleanup completed"
}

# Main function
main() {
    local command=${1:-dev}
    
    case $command in
        dev|development)
            check_prerequisites
            deploy_dev
            ;;
        prod|production)
            check_prerequisites
            deploy_prod
            ;;
        update)
            check_prerequisites
            update "${2:-development}"
            ;;
        stop)
            stop "${2:-development}"
            ;;
        status)
            status
            ;;
        logs)
            local env=${2:-development}
            if [ "$env" = "production" ]; then
                docker-compose -f "$PROD_COMPOSE_FILE" logs -f
            else
                docker-compose logs -f
            fi
            ;;
        cleanup)
            cleanup
            ;;
        help|--help|-h)
            echo "Usage: $0 {dev|prod|update|stop|status|logs|cleanup|help}"
            echo ""
            echo "Commands:"
            echo "  dev        - Deploy for development (default)"
            echo "  prod       - Deploy for production"
            echo "  update     - Update existing deployment"
            echo "  stop       - Stop deployment"
            echo "  status     - Show deployment status"
            echo "  logs       - Show service logs"
            echo "  cleanup    - Clean up Docker resources"
            echo "  help       - Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0 dev                    # Deploy for development"
            echo "  $0 prod                   # Deploy for production"
            echo "  $0 update production      # Update production deployment"
            echo "  $0 stop development       # Stop development deployment"
            echo "  $0 logs production        # Show production logs"
            ;;
        *)
            log_error "Unknown command: $command"
            echo "Use '$0 help' for usage information"
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"
