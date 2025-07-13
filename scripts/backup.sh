#!/bin/bash

# Capstone Management System - Backup Script for Docker Deployment
# This script creates backups of database and application data

set -e

# Configuration
BACKUP_DIR="./backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
COMPOSE_FILE="docker-compose.yml"
RETENTION_DAYS=30

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
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

# Check if Docker Compose is running
check_services() {
    if ! docker-compose ps | grep -q "Up"; then
        log_error "Docker Compose services are not running"
        exit 1
    fi
}

# Create backup directory
create_backup_dir() {
    mkdir -p "$BACKUP_DIR/$TIMESTAMP"
    log_info "Created backup directory: $BACKUP_DIR/$TIMESTAMP"
}

# Backup PostgreSQL database
backup_database() {
    log_info "Starting database backup..."
    
    if docker-compose exec -T database pg_dump -U capstone_user capstone_db > "$BACKUP_DIR/$TIMESTAMP/database.sql"; then
        gzip "$BACKUP_DIR/$TIMESTAMP/database.sql"
        log_info "Database backup completed: database.sql.gz"
    else
        log_error "Database backup failed"
        return 1
    fi
}

# Backup application logs
backup_logs() {
    log_info "Starting logs backup..."
    
    if [ -d "./logs" ]; then
        tar -czf "$BACKUP_DIR/$TIMESTAMP/logs.tar.gz" ./logs
        log_info "Logs backup completed: logs.tar.gz"
    else
        log_warn "No logs directory found, skipping logs backup"
    fi
}

# Backup configuration files
backup_config() {
    log_info "Starting configuration backup..."
    
    # Backup environment files
    cp .env* "$BACKUP_DIR/$TIMESTAMP/" 2>/dev/null || true
    
    # Backup Docker files
    cp docker-compose*.yml "$BACKUP_DIR/$TIMESTAMP/" 2>/dev/null || true
    
    # Backup nginx configuration
    if [ -f "./src/main/capstone-ui/nginx.conf" ]; then
        cp ./src/main/capstone-ui/nginx.conf "$BACKUP_DIR/$TIMESTAMP/"
    fi
    
    # Backup SSL certificates (if they exist)
    if [ -d "./ssl" ]; then
        cp -r ./ssl "$BACKUP_DIR/$TIMESTAMP/"
    fi
    
    log_info "Configuration backup completed"
}

# Backup Docker volumes
backup_volumes() {
    log_info "Starting volumes backup..."
    
    # Get project name (directory name)
    PROJECT_NAME=$(basename $(pwd) | tr '[:upper:]' '[:lower:]' | sed 's/[^a-z0-9]//g')
    
    # Backup PostgreSQL volume
    if docker volume ls -q | grep -q "${PROJECT_NAME}_postgres_data"; then
        docker run --rm \
            -v "${PROJECT_NAME}_postgres_data":/data \
            -v "$(pwd)/$BACKUP_DIR/$TIMESTAMP":/backup \
            alpine tar czf /backup/postgres_volume.tar.gz -C /data .
        log_info "PostgreSQL volume backup completed"
    fi
    
    # Backup Redis volume
    if docker volume ls -q | grep -q "${PROJECT_NAME}_redis_data"; then
        docker run --rm \
            -v "${PROJECT_NAME}_redis_data":/data \
            -v "$(pwd)/$BACKUP_DIR/$TIMESTAMP":/backup \
            alpine tar czf /backup/redis_volume.tar.gz -C /data .
        log_info "Redis volume backup completed"
    fi
}

# Create backup summary
create_summary() {
    cat > "$BACKUP_DIR/$TIMESTAMP/backup_info.txt" << EOF
Capstone Management System Backup
================================
Backup Date: $(date)
Backup Directory: $BACKUP_DIR/$TIMESTAMP
System Info: $(uname -a)
Docker Version: $(docker --version)
Docker Compose Version: $(docker-compose --version)

Services Status at Backup Time:
$(docker-compose ps)

Backup Contents:
$(ls -la "$BACKUP_DIR/$TIMESTAMP/")

Database Size: $(docker-compose exec database psql -U capstone_user -d capstone_db -c "SELECT pg_size_pretty(pg_database_size('capstone_db'));" -t | xargs)
EOF
    
    log_info "Backup summary created"
}

# Cleanup old backups
cleanup_old_backups() {
    log_info "Cleaning up backups older than $RETENTION_DAYS days..."
    
    find "$BACKUP_DIR" -type d -name "20*" -mtime +$RETENTION_DAYS -exec rm -rf {} + 2>/dev/null || true
    
    log_info "Cleanup completed"
}

# Create final archive
create_archive() {
    log_info "Creating final backup archive..."
    
    tar -czf "$BACKUP_DIR/backup_$TIMESTAMP.tar.gz" -C "$BACKUP_DIR" "$TIMESTAMP"
    rm -rf "$BACKUP_DIR/$TIMESTAMP"
    
    local archive_size=$(du -h "$BACKUP_DIR/backup_$TIMESTAMP.tar.gz" | cut -f1)
    log_info "Backup archive created: backup_$TIMESTAMP.tar.gz ($archive_size)"
}

# Verify backup
verify_backup() {
    log_info "Verifying backup archive..."
    
    if tar -tzf "$BACKUP_DIR/backup_$TIMESTAMP.tar.gz" > /dev/null 2>&1; then
        log_info "Backup verification successful"
        return 0
    else
        log_error "Backup verification failed"
        return 1
    fi
}

# Send backup notification (optional)
send_notification() {
    if [ -n "$BACKUP_EMAIL" ]; then
        local subject="Capstone Backup Completed - $TIMESTAMP"
        local body="Backup completed successfully at $(date). Archive: backup_$TIMESTAMP.tar.gz"
        echo "$body" | mail -s "$subject" "$BACKUP_EMAIL" 2>/dev/null || true
    fi
}

# Main backup function
main() {
    log_info "Starting Capstone Management System backup..."
    
    # Check prerequisites
    check_services
    
    # Create backup
    create_backup_dir
    backup_database || { log_error "Database backup failed"; exit 1; }
    backup_logs
    backup_config
    backup_volumes
    create_summary
    create_archive
    
    # Verify and cleanup
    if verify_backup; then
        cleanup_old_backups
        send_notification
        log_info "Backup completed successfully: $BACKUP_DIR/backup_$TIMESTAMP.tar.gz"
    else
        log_error "Backup verification failed"
        exit 1
    fi
}

# Handle script arguments
case "${1:-backup}" in
    backup)
        main
        ;;
    restore)
        if [ -z "$2" ]; then
            log_error "Usage: $0 restore <backup_file>"
            exit 1
        fi
        # Restore functionality would go here
        log_info "Restore functionality not implemented yet"
        log_info "Please refer to DOCKER_DEPLOYMENT.md for manual restore instructions"
        ;;
    list)
        log_info "Available backups:"
        ls -la "$BACKUP_DIR"/backup_*.tar.gz 2>/dev/null || log_warn "No backups found"
        ;;
    cleanup)
        cleanup_old_backups
        ;;
    *)
        echo "Usage: $0 {backup|restore|list|cleanup}"
        echo "  backup  - Create a new backup (default)"
        echo "  restore - Restore from backup file"
        echo "  list    - List available backups"
        echo "  cleanup - Remove old backups"
        exit 1
        ;;
esac
