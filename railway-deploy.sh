#!/bin/bash
# Railway.app deployment script

# Railway automatically sets PORT environment variable
export PORT=${PORT:-8080}

# Use prod profile for PostgreSQL on Railway
export SPRING_PROFILES_ACTIVE=prod

# JVM tuning for Railway
export JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0 -Djava.security.egd=file:/dev/./urandom"

echo "=== RAILWAY DEPLOYMENT ==="
echo "Port: $PORT"
echo "Spring Profile: $SPRING_PROFILES_ACTIVE"
echo "Database: PostgreSQL (Railway managed)"
echo "JVM Options: $JAVA_OPTS"

# Validate JAR file exists
if [ ! -f "target/capstone-0.0.1-SNAPSHOT.jar" ]; then
    echo "❌ ERROR: JAR file not found. Building project..."
    mvn clean package -DskipTests
fi

echo "✅ Starting Spring Boot application..."

# Start the application
exec java $JAVA_OPTS \
    -Dserver.port=$PORT \
    -Dserver.address=0.0.0.0 \
    -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
    -Djava.awt.headless=true \
    -jar target/capstone-0.0.1-SNAPSHOT.jar
