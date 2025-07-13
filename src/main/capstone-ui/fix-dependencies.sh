#!/bin/bash

echo "================================================"
echo " Fixing React Frontend Dependencies"
echo "================================================"
echo

echo "Step 1: Cleaning existing installation..."
rm -rf node_modules
rm -f package-lock.json
echo "Cleaned successfully!"
echo

echo "Step 2: Installing compatible versions..."
npm install --legacy-peer-deps
echo

echo "Step 3: Verifying installation..."
if [ -d "node_modules" ]; then
    echo "✅ Dependencies installed successfully!"
    echo
    echo "Ready to start the application!"
    echo "Run: npm start"
else
    echo "❌ Installation failed. Trying alternative method..."
    npm install --force
fi

echo
echo "================================================"
echo " Installation Complete"
echo "================================================"
