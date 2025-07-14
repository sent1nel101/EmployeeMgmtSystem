# Capstone Management System - Deployment Guide

This guide provides instructions for deploying the Capstone Management System using Docker.

## Prerequisites

- Docker and Docker Compose installed
- At least 4GB RAM available
- 10GB free disk space

## Quick Start

### Windows
```bash
# Run the deployment script
deploy.bat
```

### Linux/Mac
```bash
# Make script executable
chmod +x deploy.sh

# Run the deployment script
./deploy.sh
```

## Manual Deployment

### 1. Environment Configuration

Copy the environment template:
```bash
cp .env.production .env
```

Edit `.env` with your production values:
```bash
# Database
DB_NAME=capstone_db
DB_USER=capstone_user
DB_PASSWORD=your_secure_password_here

# JWT Configuration
JWT_SECRET=your_very_secure_jwt_secret_key_here_min_32_chars

# Other settings...
```

### 2. Deploy with Docker Compose

For development:
```bash
docker-compose up -d
```

For production:
```bash
docker-compose -f docker-compose.prod.yml up -d
```

With reverse proxy:
```bash
docker-compose -f docker-compose.prod.yml --profile proxy up -d
```

## Services

| Service | Port | Description |
|---------|------|-------------|
| Frontend | 80 | React UI with Nginx |
| Backend | 8080 | Spring Boot API |
| Database | 5432 | PostgreSQL |
| Redis | 6379 | Cache (optional) |

## Health Checks

- Frontend: http://localhost/health
- Backend: http://localhost:8080/actuator/health
- Database: Built-in PostgreSQL health check
- Redis: Built-in Redis health check

## Test Credentials

The application seeds test data automatically. See `TEST_CREDENTIALS.md` for login details:

- **Admin**: admin / admin123
- **Manager**: m.johnson / manager123
- **Employee**: j.smith / employee123

## Monitoring

View logs:
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f database
```

View running containers:
```bash
docker-compose ps
```

## Troubleshooting

### Port Conflicts
If ports are already in use, update the `.env` file:
```bash
FRONTEND_PORT=8081
BACKEND_PORT=8082
DB_PORT=5433
```

### Memory Issues
If you encounter memory issues, reduce resource limits in `docker-compose.prod.yml`.

### Database Connection Issues
1. Check if PostgreSQL is healthy:
   ```bash
   docker-compose exec database pg_isready
   ```

2. Verify environment variables:
   ```bash
   docker-compose exec backend env | grep -E "(DB_|SPRING_)"
   ```

### Frontend Build Issues
1. Clear build cache:
   ```bash
   docker-compose build --no-cache frontend
   ```

2. Check if dist folder is created correctly in the build stage.

## Production Considerations

### Security
- Change default passwords in `.env`
- Use strong JWT secret (minimum 32 characters)
- Enable HTTPS with SSL certificates
- Configure firewall rules

### Performance
- Use production profile: `SPRING_PROFILES_ACTIVE=prod`
- Enable database connection pooling
- Configure resource limits in Docker Compose
- Use Redis for session storage

### Backup
Backup PostgreSQL data:
```bash
docker-compose exec database pg_dump -U capstone_user capstone_db > backup.sql
```

Restore from backup:
```bash
docker-compose exec -T database psql -U capstone_user capstone_db < backup.sql
```

### SSL/HTTPS Setup
1. Obtain SSL certificates
2. Uncomment HTTPS server block in `nginx-proxy.conf`
3. Mount certificates in `docker-compose.prod.yml`
4. Start with proxy profile

## Stopping the Application

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: This deletes data)
docker-compose down -v

# Stop production deployment
docker-compose -f docker-compose.prod.yml down
```

## Updates and Maintenance

To update the application:
1. Pull latest code
2. Rebuild containers:
   ```bash
   docker-compose build --no-cache
   docker-compose up -d
   ```

## Support

For issues or questions:
1. Check the logs first
2. Review this deployment guide
3. Check Docker and system resources
4. Refer to application documentation

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Nginx Proxy   │────│   React Frontend │────│  Spring Backend │
│   (Optional)    │    │   (Port 80)     │    │   (Port 8080)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
                                               ┌─────────────────┐
                                               │   PostgreSQL    │
                                               │   (Port 5432)   │
                                               └─────────────────┘
```

The system uses a multi-container architecture with:
- **Frontend**: React SPA served by Nginx
- **Backend**: Spring Boot REST API
- **Database**: PostgreSQL for data persistence
- **Cache**: Redis for session management (optional)
- **Proxy**: Nginx reverse proxy for production (optional)
