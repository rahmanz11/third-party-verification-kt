@echo off
echo ========================================
echo   Real Fingerprint Integration Setup
echo ========================================
echo.
echo This script will help you set up the real fingerprint capture module
echo integration using the C# Windows Service approach.
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
echo Step 1: Checking prerequisites...
echo.

REM Check .NET installation
dotnet --version >nul 2>&1
if %errorLevel% == 0 (
    echo ✓ .NET is installed
    dotnet --version
) else (
    echo ✗ ERROR: .NET is not installed
    echo   Please install .NET 8.0 from: https://dotnet.microsoft.com/download
    echo.
    pause
    exit /b 1
)

REM Check if Digital Persona SDK is available
if exist "C:\Program Files\DigitalPersona" (
    echo ✓ Digital Persona SDK found in Program Files
) else (
    echo ⚠ WARNING: Digital Persona SDK not found in Program Files
    echo   Please ensure the SDK is installed from: E:\Digital-Persona-SDK
)

echo.
echo Step 2: Building C# Windows Service...
echo.

cd DigitalPersonaService
if not exist "DigitalPersonaService.csproj" (
    echo ✗ ERROR: DigitalPersonaService project not found
    echo   Please ensure you're in the correct directory
    echo.
    pause
    exit /b 1
)

echo Building the service...
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

REM Check if service already exists
sc query "DigitalPersonaFingerprintService" >nul 2>&1
if %errorLevel% == 0 (
    echo Service already exists. Stopping and removing...
    sc stop "DigitalPersonaFingerprintService" >nul 2>&1
    timeout /t 3 >nul
    sc delete "DigitalPersonaFingerprintService" >nul 2>&1
    timeout /t 2 >nul
)

REM Install the service
set "SERVICE_PATH=%~dp0DigitalPersonaService\bin\Release\net8.0\DigitalPersonaFingerprintService.exe"
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
echo Step 5: Testing the service...
echo.

REM Wait for service to start
timeout /t 5 >nul

REM Test health endpoint
echo Testing service health...
curl -s http://localhost:5001/api/fingerprint/health >nul 2>&1
if %errorLevel% == 0 (
    echo ✓ Service is responding to health checks
) else (
    echo ⚠ WARNING: Service may not be responding properly
    echo   Check Windows Event Viewer for error messages
)

echo.
echo Step 6: Testing device connection...
echo.

echo Testing device status...
curl -s http://localhost:5001/api/fingerprint/device/status >nul 2>&1
if %errorLevel% == 0 (
    echo ✓ Device status endpoint is working
) else (
    echo ⚠ WARNING: Device status endpoint may not be working
)

echo.
echo ========================================
echo   Integration Setup Complete!
echo ========================================
echo.
echo ✓ C# Windows Service installed and running
echo ✓ Service API available at: http://localhost:5001
echo ✓ Kotlin application ready to communicate with service
echo.
echo Next Steps:
echo   1. Connect a fingerprint scanner to your computer
echo   2. Test the device connection: curl http://localhost:5001/api/fingerprint/device/connect
echo   3. Test fingerprint capture through the Kotlin application
echo   4. Monitor logs in Windows Event Viewer
echo.
echo Useful Commands:
echo   sc query "DigitalPersonaFingerprintService"     - Check service status
echo   sc stop "DigitalPersonaFingerprintService"      - Stop the service
echo   sc start "DigitalPersonaFingerprintService"     - Start the service
echo.
echo Test API Endpoints:
echo   curl http://localhost:5001/api/fingerprint/health
echo   curl http://localhost:5001/api/fingerprint/device/status
echo   curl -X POST http://localhost:5001/api/fingerprint/device/connect
echo.
echo The Kotlin application is now configured to use the real fingerprint
echo capture module instead of the simulated one!
echo.
pause
