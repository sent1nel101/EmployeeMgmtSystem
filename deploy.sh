#!/bin/bash

# Set up ports - nginx gets the main port, backend gets +1
export NGINX_PORT=${PORT:-8080}
export BACKEND_PORT=$((NGINX_PORT + 1))

echo "Starting services..."
echo "Nginx will listen on port: $NGINX_PORT"
echo "Backend will listen on port: $BACKEND_PORT"

# Replace PORT and BACKEND_PORT in nginx config
envsubst '$$PORT $$BACKEND_PORT' < /etc/nginx/nginx.conf.template > /tmp/nginx.conf

# Start nginx in background
echo "Starting nginx..."
nginx -c /tmp/nginx.conf -g 'daemon off;' &
NGINX_PID=$!

# Give nginx a moment to start
sleep 2

# Start Spring Boot application
echo "Starting Spring Boot application..."
exec java $JAVA_OPTS -Dserver.port=$BACKEND_PORT -Dserver.address=0.0.0.0 -jar /app/app.jar