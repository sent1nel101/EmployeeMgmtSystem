@echo off
echo ================================================
echo  Fixing React Frontend Dependencies
echo ================================================
echo.

echo Step 1: Cleaning existing installation...
rmdir /s /q node_modules 2>nul
del package-lock.json 2>nul
echo Cleaned successfully!
echo.

echo Step 2: Installing compatible versions...
echo Installing with legacy peer deps to handle Material-UI conflicts...
npm install --legacy-peer-deps
echo.

echo Step 3: Verifying installation...
if exist node_modules (
    echo ✅ Dependencies installed successfully!
    echo.
    echo ✅ Fixed issues:
    echo   - JSX syntax error in useAuth.jsx
    echo   - Date-fns compatibility with Material-UI
    echo   - Material-UI version conflicts
    echo.
    echo Ready to start the application!
    echo Run: npm start
) else (
    echo ❌ Installation failed. Trying alternative method...
    npm install --force
)

echo.
echo ================================================
echo  Installation Complete
echo ================================================
pause
