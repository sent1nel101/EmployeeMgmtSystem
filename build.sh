#!/bin/bash

echo "Starting build process..."

# Build frontend
echo "Building frontend..."
cd src/main/capstone-ui
rm -rf node_modules dist package-lock.json
npm cache clean --force
npm install --legacy-peer-deps
npm run build

# Copy frontend build to Spring Boot static resources
echo "Copying frontend build to static resources..."
mkdir -p ../resources/static
cp -r dist/* ../resources/static/

# Return to root and build backend
echo "Building backend..."
cd ../../../..
mvn clean package -DskipTests

# Copy JAR file to root directory as app.jar
echo "Copying JAR file..."
echo "Current directory: $(pwd)"
echo "Target directory contents:"
ls -la target/
echo "Copying JAR..."
cp target/*.jar app.jar
echo "Root directory after copy:"
ls -la app.jar
echo "Build complete!"
