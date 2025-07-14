#!/bin/bash
# Render.com deployment script with H2 database

# Set default port if not provided
export PORT=${PORT:-8080}

# Use prod profile for H2 database
export SPRING_PROFILES_ACTIVE=prod

# JVM tuning for Render.com (reduced memory for free tier)
export JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

echo "=== RENDER.COM DEPLOYMENT WITH H2 DATABASE ==="
echo "Port: $PORT"
echo "Spring Profile: $SPRING_PROFILES_ACTIVE"
echo "Database: H2 in-memory database"
echo "JVM Options: $JAVA_OPTS"

# Validate JAR file exists
if [ ! -f "app.jar" ]; then
    echo "❌ ERROR: app.jar not found in $(pwd)"
    echo "Contents of current directory:"
    ls -la
    exit 1
fi

echo "✅ Environment validation passed"
echo "🚀 Starting Spring Boot application with H2 database..."

# Start the application
exec java $JAVA_OPTS \
    -Dserver.port=$PORT \
    -Dserver.address=0.0.0.0 \
    -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
    -Djava.awt.headless=true \
    -jar app.jar
