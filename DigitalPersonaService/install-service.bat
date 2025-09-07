@echo off
echo ========================================
echo   Digital Persona Service Installation
echo ========================================
echo.

REM Check if running as administrator
net session >nul 2>&1
if %errorLevel% == 0 (
    echo ✓ Running as administrator
) else (
    echo ✗ ERROR: This script must be run as administrator
    echo   Right-click and select "Run as administrator"
    echo.
    pause
    exit /b 1
)

echo.
echo Step 1: Stopping existing service (if any)...
echo.

REM Stop and remove existing service
sc query "DigitalPersonaFingerprintService" >nul 2>&1
if %errorLevel% == 0 (
    echo Stopping existing service...
    sc stop "DigitalPersonaFingerprintService" >nul 2>&1
    timeout /t 3 >nul
    
    echo Removing existing service...
    sc delete "DigitalPersonaFingerprintService" >nul 2>&1
    timeout /t 2 >nul
    echo ✓ Existing service removed
) else (
    echo ✓ No existing service found
)

echo.
echo Step 2: Building the service...
echo.

REM Build the service
dotnet build -c Release
if %errorLevel% neq 0 (
    echo ✗ ERROR: Build failed
    echo   Please check the build errors above
    echo.
    pause
    exit /b 1
)

echo ✓ Service built successfully

echo.
echo Step 3: Installing Windows Service...
echo.

REM Get the path to the built executable
set "SERVICE_PATH=%~dp0bin\Release\net8.0\DigitalPersonaFingerprintService.exe"

REM Install the service
sc create "DigitalPersonaFingerprintService" binPath="%SERVICE_PATH%" start=auto DisplayName="Digital Persona Fingerprint Service"
if %errorLevel% neq 0 (
    echo ✗ ERROR: Failed to create service
    echo   Please check the error message above
    echo.
    pause
    exit /b 1
)

echo ✓ Service created successfully

echo.
echo Step 4: Starting the service...
echo.

REM Start the service
sc start "DigitalPersonaFingerprintService"
if %errorLevel% neq 0 (
    echo ✗ ERROR: Failed to start service
    echo   Please check the error message above
    echo.
    pause
    exit /b 1
)

echo ✓ Service started successfully

echo.
echo Step 5: Verifying installation...
echo.

REM Wait a moment for the service to start
timeout /t 3 >nul

REM Check service status
sc query "DigitalPersonaFingerprintService" | find "RUNNING" >nul
if %errorLevel% == 0 (
    echo ✓ Service is running
) else (
    echo ⚠ WARNING: Service may not be running properly
    echo   Check Windows Event Viewer for error messages
)

echo.
echo ========================================
echo   Installation Complete!
echo ========================================
echo.
echo Service Name: DigitalPersonaFingerprintService
echo Service Path: %SERVICE_PATH%
echo API Endpoint: http://localhost:5001
echo.
echo Useful commands:
echo   sc query "DigitalPersonaFingerprintService"     - Check service status
echo   sc stop "DigitalPersonaFingerprintService"      - Stop the service
echo   sc start "DigitalPersonaFingerprintService"     - Start the service
echo   sc delete "DigitalPersonaFingerprintService"    - Remove the service
echo.
echo Test the API:
echo   curl http://localhost:5001/api/fingerprint/health
echo.
pause
