#!/bin/bash
# Environment verification script for Render.com

echo "=== RENDER.COM ENVIRONMENT VERIFICATION ==="
echo "Current directory: $(pwd)"
echo "Java version: $(java -version 2>&1 | head -1)"
echo "Available memory: $(free -m 2>/dev/null || echo 'Memory info not available')"

echo ""
echo "=== ENVIRONMENT VARIABLES ==="
echo "PORT: ${PORT:-'NOT SET'}"
echo "DATABASE_URL: ${DATABASE_URL:-'NOT SET'}"
echo "JWT_SECRET: ${JWT_SECRET:-'NOT SET'}"
echo "SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-'NOT SET'}"

echo ""
echo "=== DATABASE URL ANALYSIS ==="
if [ -n "$DATABASE_URL" ]; then
    echo "Database URL is set"
    echo "URL length: ${#DATABASE_URL}"
    if [[ $DATABASE_URL == postgres* ]]; then
        echo "✅ URL starts with 'postgres'"
    else
        echo "❌ URL does not start with 'postgres'"
    fi
    
    # Extract components
    if [[ $DATABASE_URL =~ postgres://([^:]+):([^@]+)@([^:]+):([^/]+)/(.+) ]]; then
        echo "✅ URL format appears correct"
        echo "Host: ${BASH_REMATCH[3]}"
        echo "Port: ${BASH_REMATCH[4]}"
        echo "Database: ${BASH_REMATCH[5]}"
        echo "Username: ${BASH_REMATCH[1]}"
    else
        echo "❌ URL format does not match expected pattern"
    fi
else
    echo "❌ DATABASE_URL is not set!"
    echo "This is required for the application to start"
fi

echo ""
echo "=== NETWORK CONNECTIVITY TEST ==="
if [ -n "$DATABASE_URL" ]; then
    if [[ $DATABASE_URL =~ postgres://([^:]+):([^@]+)@([^:]+):([^/]+)/(.+) ]]; then
        HOST=${BASH_REMATCH[3]}
        PORT=${BASH_REMATCH[4]}
        echo "Testing connection to $HOST:$PORT..."
        
        timeout 10 bash -c "echo >/dev/tcp/$HOST/$PORT" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "✅ Network connection to database successful"
        else
            echo "❌ Cannot connect to database host"
        fi
    fi
fi

echo ""
echo "=== FILE SYSTEM CHECK ==="
echo "Current directory contents:"
ls -la

echo ""
echo "JAR file check:"
if [ -f "app.jar" ]; then
    echo "✅ app.jar exists"
    echo "JAR size: $(ls -lh app.jar | awk '{print $5}')"
else
    echo "❌ app.jar not found"
fi

echo ""
echo "=== VERIFICATION COMPLETE ==="
