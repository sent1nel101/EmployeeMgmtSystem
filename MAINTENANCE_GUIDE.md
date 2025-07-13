# Capstone Management System - Setup and Maintenance Guide

## Table of Contents
1. [System Requirements](#system-requirements)
2. [Development Environment Setup](#development-environment-setup)
3. [Database Configuration](#database-configuration)
4. [Application Configuration](#application-configuration)
5. [Building and Running the Application](#building-and-running-the-application)
6. [Production Deployment](#production-deployment)
7. [Database Management](#database-management)
8. [Monitoring and Maintenance](#monitoring-and-maintenance)
9. [Security Configuration](#security-configuration)
10. [Backup and Recovery](#backup-and-recovery)
11. [Performance Optimization](#performance-optimization)
12. [Troubleshooting](#troubleshooting)
13. [System Administration](#system-administration)

---

## System Requirements

### Minimum Hardware Requirements
- **CPU**: 2 cores, 2.0 GHz
- **RAM**: 4 GB minimum, 8 GB recommended
- **Storage**: 10 GB available disk space
- **Network**: Stable internet connection for dependency downloads

### Software Requirements

#### Backend Requirements
- **Java Development Kit (JDK)**: Version 17 or higher
- **Apache Maven**: Version 3.6.0 or higher
- **Database**: H2 (embedded) for development, PostgreSQL/MySQL for production
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

#### Frontend Requirements
- **Node.js**: Version 16.0 or higher
- **npm**: Version 8.0 or higher (comes with Node.js)
- **Modern Web Browser**: Chrome, Firefox, Safari, or Edge (latest versions)

#### Operating System Support
- **Windows**: Windows 10 or higher
- **macOS**: macOS 10.15 or higher
- **Linux**: Ubuntu 18.04, CentOS 7, or equivalent distributions

---

## Development Environment Setup

### Installing Java Development Kit

#### Windows
1. Download OpenJDK 17 from https://adoptium.net/
2. Run the installer and follow the installation wizard
3. Set JAVA_HOME environment variable:
   - Open System Properties → Advanced → Environment Variables
   - Add new system variable: JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot
   - Add %JAVA_HOME%\bin to PATH variable
4. Verify installation: Open Command Prompt and run `java -version`

#### macOS
1. Install using Homebrew: `brew install openjdk@17`
2. Add to shell profile (.zshrc or .bash_profile):
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   export PATH=$JAVA_HOME/bin:$PATH
   ```
3. Reload shell: `source ~/.zshrc`
4. Verify installation: `java -version`

#### Linux
1. Install OpenJDK: `sudo apt install openjdk-17-jdk` (Ubuntu/Debian)
2. Set JAVA_HOME in ~/.bashrc:
   ```bash
   export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
   export PATH=$JAVA_HOME/bin:$PATH
   ```
3. Reload profile: `source ~/.bashrc`
4. Verify installation: `java -version`

### Installing Apache Maven

#### Windows
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract to C:\Program Files\Apache\maven
3. Add Maven to PATH: C:\Program Files\Apache\maven\bin
4. Verify installation: `mvn -version`

#### macOS/Linux
1. Install via package manager:
   - macOS: `brew install maven`
   - Ubuntu/Debian: `sudo apt install maven`
2. Verify installation: `mvn -version`

### Installing Node.js and npm

#### All Operating Systems
1. Download Node.js from https://nodejs.org/
2. Install the LTS (Long Term Support) version
3. Verify installation:
   ```bash
   node --version
   npm --version
   ```

### IDE Setup

#### IntelliJ IDEA
1. Download IntelliJ IDEA Community Edition
2. Install Java and Spring Boot plugins
3. Configure JDK in File → Project Structure → SDKs
4. Import the project as a Maven project

#### VS Code
1. Install Visual Studio Code
2. Install required extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - ES7+ React/Redux/React-Native snippets
   - Prettier - Code formatter

---

## Database Configuration

### Development Database (H2)

The application uses H2 embedded database for development by default. No additional setup is required.

#### H2 Console Access
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:testdb
- **Username**: sa
- **Password**: (leave blank)

#### Configuration in application.properties
```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

### Production Database Setup

#### PostgreSQL Configuration
1. Install PostgreSQL server
2. Create database and user:
   ```sql
   CREATE DATABASE capstone_db;
   CREATE USER capstone_user WITH PASSWORD 'secure_password';
   GRANT ALL PRIVILEGES ON DATABASE capstone_db TO capstone_user;
   ```

3. Update application-prod.properties:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/capstone_db
   spring.datasource.username=capstone_user
   spring.datasource.password=secure_password
   spring.datasource.driver-class-name=org.postgresql.Driver
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.hibernate.ddl-auto=validate
   ```

#### MySQL Configuration
1. Install MySQL server
2. Create database and user:
   ```sql
   CREATE DATABASE capstone_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'capstone_user'@'localhost' IDENTIFIED BY 'secure_password';
   GRANT ALL PRIVILEGES ON capstone_db.* TO 'capstone_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. Update application-prod.properties:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/capstone_db?useSSL=false&serverTimezone=UTC
   spring.datasource.username=capstone_user
   spring.datasource.password=secure_password
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
   spring.jpa.hibernate.ddl-auto=validate
   ```

---

## Application Configuration

### Backend Configuration Files

#### Main Configuration (application.properties)
```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Security Configuration
app.jwt.secret=mySecretKey
app.jwt.expiration=86400

# Logging Configuration
logging.level.com.dmcdesigns.capstone=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

#### Environment-Specific Configurations

**Development (application-dev.properties)**
```properties
# Development specific settings
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

**Production (application-prod.properties)**
```properties
# Production specific settings
spring.jpa.show-sql=false
logging.level.com.dmcdesigns.capstone=INFO
logging.level.org.hibernate.SQL=WARN

# Security Headers
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
```

### Frontend Configuration

#### Environment Variables
Create `.env` files in the React application root:

**.env.development**
```env
REACT_APP_API_BASE_URL=http://localhost:8080/api
REACT_APP_ENVIRONMENT=development
```

**.env.production**
```env
REACT_APP_API_BASE_URL=https://your-domain.com/api
REACT_APP_ENVIRONMENT=production
```

#### Package.json Scripts Configuration
```json
{
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject",
    "build:prod": "REACT_APP_ENV=production npm run build"
  }
}
```

---

## Building and Running the Application

### Backend Development

#### Running in Development Mode
1. Navigate to the project root directory
2. Start the Spring Boot application:
   ```bash
   # Using Maven
   mvn spring-boot:run
   
   # Using Maven with specific profile
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   
   # Using IDE
   # Right-click on CapstoneApplication.java and select "Run"
   ```

#### Building for Production
```bash
# Clean and package the application
mvn clean package

# Skip tests during build (if needed)
mvn clean package -DskipTests

# Build with specific profile
mvn clean package -Pprod
```

### Frontend Development

#### Installing Dependencies
```bash
# Navigate to frontend directory
cd src/main/capstone-ui

# Install dependencies
npm install

# Install specific dependencies (if needed)
npm install @mui/material @emotion/react @emotion/styled
npm install @mui/icons-material
npm install axios react-router-dom
npm install chart.js react-chartjs-2
```

#### Running Development Server
```bash
# Start development server
npm start

# Start with specific port
PORT=3000 npm start

# Start with environment variables
REACT_APP_API_BASE_URL=http://localhost:8080/api npm start
```

#### Building for Production
```bash
# Create production build
npm run build

# Serve production build locally (for testing)
npx serve -s build -p 3000
```

### Full Application Startup

#### Development Environment
1. Start the backend:
   ```bash
   mvn spring-boot:run
   ```

2. In a new terminal, start the frontend:
   ```bash
   cd src/main/capstone-ui
   npm start
   ```

3. Access the application:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api
   - H2 Database Console: http://localhost:8080/h2-console

---

## Production Deployment

### Standalone JAR Deployment

#### Building the Application
```bash
# Build the backend
mvn clean package -Pprod

# Build the frontend
cd src/main/capstone-ui
npm run build

# Copy frontend build to backend static resources
cp -r build/* ../../../target/classes/static/
```

#### Running the Application
```bash
# Run with production profile
java -jar -Dspring.profiles.active=prod target/capstone-0.0.1-SNAPSHOT.jar

# Run with custom configuration
java -jar -Dspring.profiles.active=prod -Dserver.port=8080 target/capstone-0.0.1-SNAPSHOT.jar

# Run as background service
nohup java -jar -Dspring.profiles.active=prod target/capstone-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

### Docker Deployment

#### Dockerfile for Backend
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/capstone-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

#### Docker Compose Configuration
```yaml
version: '3.8'
services:
  database:
    image: postgres:13
    environment:
      POSTGRES_DB: capstone_db
      POSTGRES_USER: capstone_user
      POSTGRES_PASSWORD: secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://database:5432/capstone_db
    depends_on:
      - database

volumes:
  postgres_data:
```

#### Deployment Commands
```bash
# Build and start services
docker-compose up --build -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Update application
docker-compose down
docker-compose up --build -d
```

### Web Server Configuration

#### Nginx Configuration
```nginx
server {
    listen 80;
    server_name your-domain.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl;
    server_name your-domain.com;

    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;

    # Frontend static files
    location / {
        root /var/www/capstone/frontend;
        try_files $uri $uri/ /index.html;
    }

    # Backend API proxy
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## Database Management

### Data Seeding

#### Initial Data Setup
The application includes a DataSeeder component that automatically creates initial data:

```java
@Component
public class DataSeeder implements CommandLineRunner {
    // Creates admin user, sample departments, employees, and projects
}
```

#### Manual Data Seeding
```sql
-- Create admin user
INSERT INTO users (first_name, last_name, email, username, password, department, user_type) 
VALUES ('Admin', 'User', 'admin@ourcompany.com', 'admin', '$2a$10$...', 'IT', 'Admin');

-- Create sample departments
INSERT INTO departments (name, description) VALUES 
('Information Technology', 'IT Department'),
('Human Resources', 'HR Department'),
('Finance', 'Finance Department');
```

### Database Migrations

#### Flyway Integration (Optional)
1. Add Flyway dependency to pom.xml:
   ```xml
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-core</artifactId>
   </dependency>
   ```

2. Create migration files in src/main/resources/db/migration/:
   ```sql
   -- V1__Initial_schema.sql
   CREATE TABLE users (
       id BIGINT PRIMARY KEY,
       first_name VARCHAR(50) NOT NULL,
       -- ... other columns
   );
   ```

### Database Backup and Restore

#### PostgreSQL Backup
```bash
# Create backup
pg_dump -U capstone_user -h localhost capstone_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Automated backup script
#!/bin/bash
BACKUP_DIR="/var/backups/capstone"
DB_NAME="capstone_db"
DB_USER="capstone_user"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

pg_dump -U $DB_USER -h localhost $DB_NAME > $BACKUP_DIR/backup_$TIMESTAMP.sql
gzip $BACKUP_DIR/backup_$TIMESTAMP.sql

# Keep only last 30 days of backups
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +30 -delete
```

#### PostgreSQL Restore
```bash
# Restore from backup
psql -U capstone_user -h localhost capstone_db < backup_20231201_120000.sql

# Restore from compressed backup
gunzip -c backup_20231201_120000.sql.gz | psql -U capstone_user -h localhost capstone_db
```

---

## Monitoring and Maintenance

### Application Health Monitoring

#### Spring Boot Actuator
1. Add dependency to pom.xml:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
   ```

2. Configure endpoints in application.properties:
   ```properties
   management.endpoints.web.exposure.include=health,info,metrics,env
   management.endpoint.health.show-details=when-authorized
   management.info.env.enabled=true
   ```

3. Access monitoring endpoints:
   - Health: http://localhost:8080/actuator/health
   - Metrics: http://localhost:8080/actuator/metrics
   - Info: http://localhost:8080/actuator/info

### Log Management

#### Logging Configuration
```xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="dev">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>

    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>logs/capstone.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>logs/capstone.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                <maxFileSize>10MB</maxFileSize>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>
</configuration>
```

#### Log Monitoring Script
```bash
#!/bin/bash
# monitor_logs.sh

LOG_FILE="/var/log/capstone/capstone.log"
ERROR_THRESHOLD=10
WARN_EMAIL="admin@company.com"

# Count errors in the last hour
ERROR_COUNT=$(grep -c "ERROR" $LOG_FILE | tail -n 100)

if [ $ERROR_COUNT -gt $ERROR_THRESHOLD ]; then
    echo "High error count detected: $ERROR_COUNT errors" | mail -s "Capstone App Alert" $WARN_EMAIL
fi

# Monitor disk space
DISK_USAGE=$(df /var/log | tail -1 | awk '{print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 80 ]; then
    echo "Disk space warning: $DISK_USAGE% used" | mail -s "Disk Space Alert" $WARN_EMAIL
fi
```

### Performance Monitoring

#### Database Performance
```sql
-- Monitor slow queries (PostgreSQL)
SELECT query, mean_time, calls, total_time 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;

-- Check database connections
SELECT count(*) as active_connections 
FROM pg_stat_activity 
WHERE state = 'active';

-- Monitor table sizes
SELECT schemaname, tablename, 
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE schemaname = 'public' 
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

#### Application Metrics
```bash
# Monitor memory usage
jstat -gc -t [PID] 5s

# Monitor thread usage
jstack [PID] > thread_dump.txt

# Monitor heap usage
jmap -histo [PID] | head -20
```

---

## Security Configuration

### SSL/TLS Configuration

#### Self-Signed Certificate (Development)
```bash
# Generate keystore
keytool -genkeypair -alias capstone -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore capstone.p12 -validity 3650

# Configure in application.properties
server.ssl.key-store=classpath:capstone.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=capstone
server.port=8443
```

#### Production SSL Certificate
```properties
# application-prod.properties
server.ssl.key-store=/etc/ssl/certs/capstone.jks
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=JKS
server.ssl.key-alias=capstone
server.port=8443

# Redirect HTTP to HTTPS
server.ssl.enabled=true
security.require-ssl=true
```

### Security Headers Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers
            .frameOptions().deny()
            .contentTypeOptions().and()
            .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                .maxAgeInSeconds(31536000)
                .includeSubdomains(true))
            .and()
        );
        return http.build();
    }
}
```

### Password Policy Configuration
```properties
# Password requirements
app.security.password.min-length=8
app.security.password.require-uppercase=true
app.security.password.require-lowercase=true
app.security.password.require-numbers=true
app.security.password.require-special-chars=true

# Account lockout
app.security.lockout.max-attempts=5
app.security.lockout.duration=30

# Session configuration
server.servlet.session.timeout=30m
server.servlet.session.cookie.max-age=1800
```

---

## Backup and Recovery

### Automated Backup Strategy

#### Daily Backup Script
```bash
#!/bin/bash
# daily_backup.sh

BACKUP_DIR="/var/backups/capstone"
APP_DIR="/opt/capstone"
DB_NAME="capstone_db"
DB_USER="capstone_user"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Create backup directory
mkdir -p $BACKUP_DIR/$TIMESTAMP

# Database backup
pg_dump -U $DB_USER -h localhost $DB_NAME > $BACKUP_DIR/$TIMESTAMP/database.sql

# Application files backup
tar -czf $BACKUP_DIR/$TIMESTAMP/application.tar.gz $APP_DIR

# Log files backup
tar -czf $BACKUP_DIR/$TIMESTAMP/logs.tar.gz /var/log/capstone

# Configuration backup
cp /etc/capstone/* $BACKUP_DIR/$TIMESTAMP/

# Compress entire backup
tar -czf $BACKUP_DIR/backup_$TIMESTAMP.tar.gz -C $BACKUP_DIR $TIMESTAMP
rm -rf $BACKUP_DIR/$TIMESTAMP

# Cleanup old backups (keep 30 days)
find $BACKUP_DIR -name "backup_*.tar.gz" -mtime +30 -delete

echo "Backup completed: backup_$TIMESTAMP.tar.gz"
```

#### Cron Job Configuration
```bash
# Edit crontab
crontab -e

# Add daily backup at 2 AM
0 2 * * * /opt/scripts/daily_backup.sh >> /var/log/backup.log 2>&1

# Add weekly full backup on Sundays at 1 AM
0 1 * * 0 /opt/scripts/weekly_backup.sh >> /var/log/backup.log 2>&1
```

### Disaster Recovery

#### Recovery Checklist
1. **Assess the situation**
   - Identify the scope of the failure
   - Determine if it's a partial or complete system failure
   - Estimate downtime requirements

2. **Database Recovery**
   ```bash
   # Stop application
   systemctl stop capstone
   
   # Restore database
   dropdb capstone_db
   createdb capstone_db
   psql -U capstone_user capstone_db < backup_database.sql
   ```

3. **Application Recovery**
   ```bash
   # Restore application files
   tar -xzf application_backup.tar.gz -C /opt/
   
   # Restore configuration
   cp backup_config/* /etc/capstone/
   
   # Restart services
   systemctl start capstone
   ```

4. **Verification Steps**
   - Test database connectivity
   - Verify application startup
   - Check critical functionality
   - Monitor logs for errors
   - Notify users of service restoration

---

## Performance Optimization

### Database Optimization

#### Index Creation
```sql
-- Create indexes for frequently queried columns
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_department ON users(department);
CREATE INDEX idx_employees_hire_date ON employees(hire_date);
CREATE INDEX idx_performance_reviews_employee_date ON performance_reviews(employee_id, review_date);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_start_date ON projects(start_date);
```

#### Query Optimization
```sql
-- Monitor and optimize slow queries
EXPLAIN ANALYZE SELECT * FROM users WHERE department = 'IT';

-- Use LIMIT for large datasets
SELECT * FROM employees ORDER BY hire_date DESC LIMIT 50;

-- Optimize JOIN queries
SELECT u.first_name, u.last_name, pr.rating 
FROM users u 
INNER JOIN performance_reviews pr ON u.id = pr.employee_id 
WHERE pr.review_date >= '2023-01-01';
```

### Application Performance

#### JVM Tuning
```bash
# Production JVM settings
java -Xms2g -Xmx4g -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/var/log/capstone/ \
     -jar capstone.jar
```

#### Connection Pool Optimization
```properties
# HikariCP configuration
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.leak-detection-threshold=60000
```

#### Caching Configuration
```java
@EnableCaching
@Configuration
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30)));
        return cacheManager;
    }
}
```

### Frontend Optimization

#### Build Optimization
```json
{
  "scripts": {
    "build:analyze": "npm run build && npx bundle-analyzer build/static/js/*.js",
    "build:prod": "GENERATE_SOURCEMAP=false npm run build"
  }
}
```

#### Performance Monitoring
```javascript
// Add to index.js
import { getCLS, getFID, getFCP, getLCP, getTTFB } from 'web-vitals';

function sendToAnalytics(metric) {
  console.log(metric);
  // Send to analytics service
}

getCLS(sendToAnalytics);
getFID(sendToAnalytics);
getFCP(sendToAnalytics);
getLCP(sendToAnalytics);
getTTFB(sendToAnalytics);
```

---

## Troubleshooting

### Common Issues and Solutions

#### Application Won't Start

**Issue**: Port already in use
```
Error: Port 8080 is already in use
```
**Solution**:
```bash
# Find process using the port
lsof -i :8080
netstat -tulpn | grep :8080

# Kill the process
kill -9 [PID]

# Or use different port
java -jar -Dserver.port=8081 capstone.jar
```

**Issue**: Database connection failure
```
Error: Connection to database failed
```
**Solution**:
1. Verify database is running
2. Check connection parameters
3. Verify user permissions
4. Test connectivity:
   ```bash
   psql -U capstone_user -h localhost capstone_db
   ```

#### Memory Issues

**Issue**: OutOfMemoryError
```
java.lang.OutOfMemoryError: Java heap space
```
**Solution**:
```bash
# Increase heap size
java -Xms1g -Xmx2g -jar capstone.jar

# Monitor memory usage
jstat -gc [PID] 5s

# Generate heap dump for analysis
jmap -dump:format=b,file=heap.hprof [PID]
```

#### Performance Issues

**Issue**: Slow database queries
**Solution**:
1. Enable query logging:
   ```properties
   spring.jpa.show-sql=true
   logging.level.org.hibernate.SQL=DEBUG
   ```

2. Analyze slow queries:
   ```sql
   SELECT query, mean_time, calls 
   FROM pg_stat_statements 
   ORDER BY mean_time DESC LIMIT 10;
   ```

3. Add indexes for frequently queried columns

**Issue**: High CPU usage
**Solution**:
1. Generate thread dump:
   ```bash
   jstack [PID] > thread_dump.txt
   ```

2. Analyze using tools like Eclipse MAT or VisualVM

3. Check for infinite loops or blocking operations

#### Frontend Issues

**Issue**: Build failures
```
Error: Module not found
```
**Solution**:
```bash
# Clear node modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Clear npm cache
npm cache clean --force
```

**Issue**: API connection errors
**Solution**:
1. Verify backend is running
2. Check CORS configuration
3. Verify API base URL in environment variables
4. Check network connectivity

### Diagnostic Commands

#### System Health Check
```bash
#!/bin/bash
# health_check.sh

echo "=== System Health Check ==="

# Check Java process
if pgrep -f "capstone.jar" > /dev/null; then
    echo "✓ Application is running"
    PID=$(pgrep -f "capstone.jar")
    echo "  PID: $PID"
    echo "  Memory: $(ps -p $PID -o %mem --no-headers)%"
    echo "  CPU: $(ps -p $PID -o %cpu --no-headers)%"
else
    echo "✗ Application is not running"
fi

# Check database connectivity
if pg_isready -h localhost -p 5432 -U capstone_user; then
    echo "✓ Database is accessible"
else
    echo "✗ Database is not accessible"
fi

# Check disk space
DISK_USAGE=$(df / | tail -1 | awk '{print $5}' | sed 's/%//')
if [ $DISK_USAGE -lt 80 ]; then
    echo "✓ Disk space: $DISK_USAGE% used"
else
    echo "⚠ Disk space warning: $DISK_USAGE% used"
fi

# Check memory usage
MEM_USAGE=$(free | grep Mem | awk '{printf("%.1f", $3/$2 * 100.0)}')
echo "✓ Memory usage: $MEM_USAGE%"

# Check application endpoint
if curl -f -s http://localhost:8080/actuator/health > /dev/null; then
    echo "✓ Application health endpoint responds"
else
    echo "✗ Application health endpoint not responding"
fi
```

---

## System Administration

### User Management

#### Creating System Admin User
```sql
-- Create admin user in database
INSERT INTO users (first_name, last_name, email, username, password, department, user_type) 
VALUES ('System', 'Administrator', 'admin@ourcompany.com', 'sysadmin', 
        '$2a$10$encrypted_password_hash', 'IT', 'Admin');
```

#### Resetting User Passwords
```sql
-- Reset user password (use BCrypt hash)
UPDATE users 
SET password = '$2a$10$new_encrypted_password_hash' 
WHERE username = 'user.name';
```

### System Maintenance

#### Log Rotation
```bash
# /etc/logrotate.d/capstone
/var/log/capstone/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    copytruncate
}
```

#### Service Management
```bash
# Create systemd service file: /etc/systemd/system/capstone.service
[Unit]
Description=Capstone Management System
After=network.target

[Service]
Type=simple
User=capstone
WorkingDirectory=/opt/capstone
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /opt/capstone/capstone.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target

# Enable and start service
systemctl enable capstone
systemctl start capstone
systemctl status capstone
```

### Monitoring and Alerting

#### System Monitoring Script
```bash
#!/bin/bash
# system_monitor.sh

# Configuration
ALERT_EMAIL="admin@company.com"
LOG_FILE="/var/log/capstone/monitor.log"
APP_PID=$(pgrep -f "capstone.jar")

# Function to send alert
send_alert() {
    local message="$1"
    echo "$(date): ALERT - $message" >> $LOG_FILE
    echo "$message" | mail -s "Capstone System Alert" $ALERT_EMAIL
}

# Check if application is running
if [ -z "$APP_PID" ]; then
    send_alert "Application is not running"
    exit 1
fi

# Check memory usage
MEM_USAGE=$(ps -p $APP_PID -o %mem --no-headers | tr -d ' ')
if (( $(echo "$MEM_USAGE > 80" | bc -l) )); then
    send_alert "High memory usage: $MEM_USAGE%"
fi

# Check disk space
DISK_USAGE=$(df / | tail -1 | awk '{print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 85 ]; then
    send_alert "Low disk space: $DISK_USAGE% used"
fi

# Check error count in logs
ERROR_COUNT=$(tail -1000 /var/log/capstone/capstone.log | grep -c "ERROR")
if [ $ERROR_COUNT -gt 5 ]; then
    send_alert "High error count in logs: $ERROR_COUNT errors"
fi

echo "$(date): System check completed" >> $LOG_FILE
```

#### Health Check Endpoint Monitoring
```bash
#!/bin/bash
# health_monitor.sh

HEALTH_URL="http://localhost:8080/actuator/health"
TIMEOUT=10

response=$(curl -s -w "%{http_code}" --max-time $TIMEOUT "$HEALTH_URL")
http_code="${response: -3}"

if [ "$http_code" != "200" ]; then
    echo "Health check failed. HTTP code: $http_code"
    systemctl restart capstone
    sleep 30
    
    # Check again after restart
    response=$(curl -s -w "%{http_code}" --max-time $TIMEOUT "$HEALTH_URL")
    http_code="${response: -3}"
    
    if [ "$http_code" != "200" ]; then
        echo "Application failed to recover. Manual intervention required."
        exit 1
    fi
fi

echo "Health check passed"
```

---

This comprehensive maintenance guide provides all the necessary information for setting up, configuring, deploying, and maintaining the Capstone Management System. Regular review and updates of these procedures ensure optimal system performance and reliability.
