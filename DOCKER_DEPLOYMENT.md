# Docker Deployment Guide for Capstone Management System

## Quick Start

### Prerequisites
- Docker Engine 20.10+
- Docker Compose 2.0+
- 4GB RAM minimum
- 10GB disk space

### Development Deployment
```bash
# Clone the repository
git clone <repository-url>
cd d424-software-engineering-capstone

# Start all services
docker-compose up -d

# Access the application
# Frontend: http://localhost
# Backend API: http://localhost:8080/api
# Database: localhost:5432
```

---

## Detailed Setup Instructions

### 1. Environment Configuration

#### Copy Environment File
```bash
# For development
cp .env .env.local

# For production
cp .env.prod .env.local
```

#### Edit Environment Variables
```bash
nano .env.local
```

**Required Changes for Production:**
- `DB_PASSWORD`: Change to secure password
- `JWT_SECRET`: Change to random 32+ character string
- `REDIS_PASSWORD`: Change to secure password
- `FRONTEND_API_URL`: Update to your domain

### 2. Build and Start Services

#### Development Environment
```bash
# Build and start all services
docker-compose up --build -d

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f database
```

#### Production Environment
```bash
# Use production compose file
docker-compose -f docker-compose.prod.yml up --build -d

# Check service status
docker-compose -f docker-compose.prod.yml ps

# Monitor logs
docker-compose -f docker-compose.prod.yml logs -f
```

### 3. Service Access

| Service | Development | Production |
|---------|-------------|------------|
| Frontend | http://localhost | http://your-domain.com |
| Backend API | http://localhost:8080/api | http://your-domain.com/api |
| Database | localhost:5432 | Internal only |
| Redis | localhost:6379 | Internal only |

### 4. Test User Credentials

The application automatically creates test users on first startup. **See TEST_CREDENTIALS.md for the complete list**.

**Quick Access Accounts:**
- **Admin**: `admin` / `admin123` (Full system access)
- **HR Director**: `hr.director` / `hr123` (Employee management)
- **Manager**: `manager` / `manager123` (Team oversight)
- **Employee**: `employee` / `employee123` (Limited access)

**Note**: These test accounts are created for review and testing purposes. They are available in both development and production environments.

---

## Container Details

### Backend Container (Spring Boot)
- **Image**: Custom built from Dockerfile
- **Port**: 8080
- **Health Check**: `/actuator/health`
- **Memory**: 2GB limit
- **CPU**: 1 core limit

#### Environment Variables
```env
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/capstone_db
SPRING_DATASOURCE_USERNAME=capstone_user
SPRING_DATASOURCE_PASSWORD=<secure_password>
JWT_SECRET=<secure_jwt_secret>
```

### Frontend Container (React + Nginx)
- **Image**: Custom built with Nginx
- **Port**: 80 (HTTP), 443 (HTTPS)
- **Health Check**: `/health`
- **Memory**: 512MB limit

#### Features
- Gzip compression enabled
- Security headers configured
- API proxy to backend
- Client-side routing support

### Database Container (PostgreSQL)
- **Image**: postgres:15-alpine
- **Port**: 5432
- **Data Persistence**: Docker volume
- **Memory**: 1GB limit

#### Configuration
- Automatic initialization with `init-db.sql`
- Performance optimizations applied
- Health check function created

### Cache Container (Redis)
- **Image**: redis:7-alpine
- **Port**: 6379
- **Data Persistence**: Docker volume
- **Memory**: 256MB limit

---

## Management Commands

### Start/Stop Services
```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# Restart specific service
docker-compose restart backend

# View service status
docker-compose ps
```

### Scaling Services
```bash
# Scale backend to 3 instances
docker-compose up -d --scale backend=3

# Scale with load balancer (production)
docker-compose -f docker-compose.prod.yml up -d --scale backend=3
```

### Updates and Rebuilding
```bash
# Update and rebuild all services
docker-compose down
docker-compose pull
docker-compose up --build -d

# Update specific service
docker-compose up --build -d backend
```

---

## Data Management

### Database Operations

#### Backup Database
```bash
# Create backup
docker-compose exec database pg_dump -U capstone_user capstone_db > backup.sql

# Automated backup script
docker-compose exec database sh -c 'pg_dump -U capstone_user capstone_db | gzip > /backups/backup_$(date +%Y%m%d_%H%M%S).sql.gz'
```

#### Restore Database
```bash
# Stop application first
docker-compose stop backend

# Restore from backup
docker-compose exec -T database psql -U capstone_user capstone_db < backup.sql

# Start application
docker-compose start backend
```

#### Database Console Access
```bash
# Connect to database
docker-compose exec database psql -U capstone_user -d capstone_db

# View database logs
docker-compose logs database
```

### Volume Management

#### List Volumes
```bash
docker volume ls
```

#### Backup Volumes
```bash
# Backup PostgreSQL data
docker run --rm -v d424-software-engineering-capstone_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz -C /data .

# Backup Redis data
docker run --rm -v d424-software-engineering-capstone_redis_data:/data -v $(pwd):/backup alpine tar czf /backup/redis_backup.tar.gz -C /data .
```

#### Restore Volumes
```bash
# Restore PostgreSQL data
docker run --rm -v d424-software-engineering-capstone_postgres_data:/data -v $(pwd):/backup alpine tar xzf /backup/postgres_backup.tar.gz -C /data

# Restore Redis data
docker run --rm -v d424-software-engineering-capstone_redis_data:/data -v $(pwd):/backup alpine tar xzf /backup/redis_backup.tar.gz -C /data
```

---

## Monitoring and Logging

### Health Checks
```bash
# Check all service health
docker-compose ps

# Check specific service health
curl http://localhost:8080/actuator/health
curl http://localhost/health
```

### Log Management
```bash
# View all logs
docker-compose logs

# Follow logs in real-time
docker-compose logs -f

# View logs for specific service
docker-compose logs -f backend

# View last 50 lines
docker-compose logs --tail=50 backend

# Search logs
docker-compose logs backend | grep ERROR
```

### Resource Monitoring
```bash
# View resource usage
docker stats

# View container details
docker-compose top

# Check disk usage
docker system df
```

---

## Security Configuration

### SSL/HTTPS Setup

#### 1. Obtain SSL Certificates
```bash
# Create SSL directory
mkdir -p ssl

# Option 1: Let's Encrypt (recommended)
certbot certonly --standalone -d your-domain.com
cp /etc/letsencrypt/live/your-domain.com/fullchain.pem ssl/
cp /etc/letsencrypt/live/your-domain.com/privkey.pem ssl/

# Option 2: Self-signed (development only)
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout ssl/privkey.pem -out ssl/fullchain.pem
```

#### 2. Update Nginx Configuration
```nginx
# Add to nginx.conf
server {
    listen 443 ssl;
    ssl_certificate /etc/nginx/ssl/fullchain.pem;
    ssl_certificate_key /etc/nginx/ssl/privkey.pem;
    # ... rest of configuration
}
```

### Security Best Practices

#### 1. Change Default Passwords
```bash
# Update .env.local with secure passwords
DB_PASSWORD=your_secure_db_password_here
JWT_SECRET=your_very_long_random_jwt_secret_key_here
REDIS_PASSWORD=your_secure_redis_password_here
```

#### 2. Network Security
```bash
# Create custom network (optional)
docker network create capstone-secure-network

# Update docker-compose.yml to use custom network
networks:
  default:
    external:
      name: capstone-secure-network
```

#### 3. Container Security
```bash
# Run containers as non-root (already configured)
# Use specific image versions (already configured)
# Enable security options
docker-compose up -d --security-opt no-new-privileges
```

---

## Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Check what's using the port
sudo lsof -i :80
sudo lsof -i :8080

# Kill the process
sudo kill -9 <PID>

# Or change port in .env.local
FRONTEND_PORT=8081
BACKEND_PORT=8082
```

#### Database Connection Issues
```bash
# Check database logs
docker-compose logs database

# Verify database is running
docker-compose ps database

# Test database connection
docker-compose exec database psql -U capstone_user -d capstone_db -c "SELECT 1;"
```

#### Out of Memory Errors
```bash
# Check container resource usage
docker stats

# Increase memory limits in docker-compose.yml
deploy:
  resources:
    limits:
      memory: 4G  # Increase as needed
```

#### Build Failures
```bash
# Clean Docker cache
docker system prune -a

# Rebuild without cache
docker-compose build --no-cache

# Check available disk space
df -h
```

### Debugging Commands

#### Container Shell Access
```bash
# Access backend container
docker-compose exec backend bash

# Access database container
docker-compose exec database bash

# Access frontend container
docker-compose exec frontend sh
```

#### Log Analysis
```bash
# Export logs to file
docker-compose logs > application_logs.txt

# Search for errors
docker-compose logs | grep -i error

# Monitor logs in real-time
docker-compose logs -f | grep -E "(ERROR|WARN)"
```

---

## Production Deployment Checklist

### Pre-Deployment
- [ ] Update all environment variables in `.env.prod`
- [ ] Change all default passwords
- [ ] Obtain SSL certificates
- [ ] Configure domain DNS settings
- [ ] Test backup and restore procedures

### Deployment
- [ ] Deploy using production compose file
- [ ] Verify all services start successfully
- [ ] Test application functionality
- [ ] Configure automated backups
- [ ] Set up monitoring and alerting

### Post-Deployment
- [ ] Test SSL/HTTPS configuration
- [ ] Verify security headers
- [ ] Test performance under load
- [ ] Document access credentials
- [ ] Create operational runbooks

---

## Maintenance Commands

### Regular Maintenance
```bash
# Update container images
docker-compose pull
docker-compose up -d

# Clean up unused resources
docker system prune

# Backup data (schedule daily)
./scripts/backup.sh

# Check log file sizes
du -sh logs/
```

### Updates and Patches
```bash
# Update application code
git pull origin main

# Rebuild and deploy
docker-compose down
docker-compose up --build -d

# Verify deployment
curl http://localhost/health
curl http://localhost:8080/actuator/health
```

---

For additional support or questions, refer to the MAINTENANCE_GUIDE.md or contact your system administrator.
