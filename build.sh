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

# List JAR files for verification
echo "JAR files in target directory:"
ls -la target/*.jar
echo "Build complete!"
