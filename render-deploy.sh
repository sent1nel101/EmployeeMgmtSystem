#!/bin/bash
# Render.com deployment script

# Set default port if not provided
export PORT=${PORT:-8080}

# Use render profile for Render.com specific configuration
export SPRING_PROFILES_ACTIVE=render

# JVM tuning for Render.com (reduced memory for free tier)
export JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

echo "Starting application on port $PORT..."
echo "Spring Profile: $SPRING_PROFILES_ACTIVE"
echo "Database URL: $DATABASE_URL"

# Validate required environment variables
if [ -z "$DATABASE_URL" ]; then
    echo "ERROR: DATABASE_URL environment variable is not set"
    exit 1
fi

# Start the application
exec java $JAVA_OPTS -Dserver.port=$PORT -Dserver.address=0.0.0.0 -jar app.jar
