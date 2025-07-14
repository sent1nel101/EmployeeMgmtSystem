#!/bin/bash
# Render.com deployment script

# Set default port if not provided
export PORT=${PORT:-8080}

# Database configuration for Render.com
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=${DATABASE_URL:-jdbc:postgresql://localhost:5432/capstone_db}
export SPRING_DATASOURCE_USERNAME=${DATABASE_USERNAME:-capstone_user}
export SPRING_DATASOURCE_PASSWORD=${DATABASE_PASSWORD:-capstone_password}
export JWT_SECRET=${JWT_SECRET:-myVerySecretKeyForJWTTokenGeneration123456789}
export JWT_EXPIRATION=${JWT_EXPIRATION:-86400}

# JVM tuning for Render.com (reduced memory for free tier)
export JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

echo "Starting application on port $PORT..."
echo "Database URL: $SPRING_DATASOURCE_URL"

# Start the application
exec java $JAVA_OPTS -Dserver.port=$PORT -Dserver.address=0.0.0.0 -jar app.jar
