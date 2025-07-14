#!/bin/bash
# Render.com deployment script

# Set default port if not provided
export PORT=${PORT:-8080}

# Use render profile for Render.com specific configuration
export SPRING_PROFILES_ACTIVE=render

# Run environment verification
echo "Running environment verification..."
chmod +x verify-environment.sh 2>/dev/null || true
./verify-environment.sh 2>/dev/null || echo "Environment verification script not found"

# JVM tuning for Render.com (reduced memory for free tier)
export JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

echo ""
echo "=== APPLICATION STARTUP ==="
echo "Starting application on port $PORT..."
echo "Spring Profile: $SPRING_PROFILES_ACTIVE"
echo "JVM Options: $JAVA_OPTS"

# Validate required environment variables
if [ -z "$DATABASE_URL" ]; then
    echo "❌ ERROR: DATABASE_URL environment variable is not set"
    echo "Please set the DATABASE_URL environment variable in your Render.com service settings"
    exit 1
fi

# Additional validation
if [ ! -f "app.jar" ]; then
    echo "❌ ERROR: app.jar not found in $(pwd)"
    echo "Contents of current directory:"
    ls -la
    exit 1
fi

echo "✅ Environment validation passed"
echo "🚀 Starting Spring Boot application..."

# Start the application with more verbose logging
exec java $JAVA_OPTS \
    -Dserver.port=$PORT \
    -Dserver.address=0.0.0.0 \
    -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
    -Djava.awt.headless=true \
    -jar app.jar
