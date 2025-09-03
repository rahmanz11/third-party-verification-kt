@echo off
echo ========================================
echo   Plan B: Digital Persona Integration
echo ========================================
echo.
echo This script will help you get started with Plan B
echo (C# Windows Service approach) for fingerprint scanner integration.
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

REM Check if Digital Persona SDK is available locally
if exist "DigitalPersonaService\SDK\Bin\DPFPDevNET.dll" (
    echo ✓ Digital Persona SDK found locally in project
) else (
    echo ⚠ WARNING: Digital Persona SDK not found locally
    echo   Please ensure the SDK files are copied to DigitalPersonaService\SDK\Bin\
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
    sc delete "DigitalPersonaFingerprintService" >nul 2>&1
)

REM Install the service
echo Installing the service...
sc create "DigitalPersonaFingerprintService" binPath="%~dp0DigitalPersonaService\bin\Release\net8.0\DigitalPersonaService.exe" start=auto DisplayName="Digital Persona Fingerprint Service"

if %errorLevel% == 0 (
    echo ✓ Service installed successfully
    echo.
    echo Step 4: Starting the service...
    echo.
    
    sc start "DigitalPersonaFingerprintService"
    
    if %errorLevel% == 0 (
        echo ✓ Service started successfully
        echo.
        echo Step 5: Testing the service...
        echo.
        
        REM Wait a moment for service to fully start
        timeout /t 3 /nobreak >nul
        
        REM Test health endpoint
        echo Testing health endpoint...
        curl -s http://localhost:5001/api/fingerprint/health >nul 2>&1
        if %errorLevel% == 0 (
            echo ✓ Service is responding to HTTP requests
        ) else (
            echo ⚠ Service may not be fully ready yet
        )
        
        echo.
        echo Step 6: Service status...
        echo.
        sc query "DigitalPersonaFingerprintService"
        
    ) else (
        echo ✗ ERROR: Failed to start service
        echo   Check Windows Event Viewer for error details
    )
) else (
    echo ✗ ERROR: Failed to install service
    echo   Please check the error message above
)

echo.
echo ========================================
echo   Setup Complete!
echo ========================================
echo.
echo The Digital Persona Fingerprint Service has been installed and started.
echo.
echo Service Details:
echo   - Name: DigitalPersonaFingerprintService
echo   - URL: http://localhost:5001
echo   - Auto-start: Enabled
echo.
echo Next Steps:
echo   1. Test the service: curl http://localhost:5001/api/fingerprint/health
echo   2. Connect to fingerprint device: curl -X POST http://localhost:5001/api/fingerprint/device/connect
echo   3. Test fingerprint capture through your Kotlin application
echo.
echo Service Management:
echo   - Start: sc start "DigitalPersonaFingerprintService"
echo   - Stop:  sc stop "DigitalPersonaFingerprintService"
echo   - Status: sc query "DigitalPersonaFingerprintService"
echo   - Remove: sc delete "DigitalPersonaFingerprintService"
echo.
echo For troubleshooting, check:
echo   - Windows Event Viewer
echo   - Service logs in DigitalPersonaService directory
echo   - This guide: PLAN_B_INTEGRATION_GUIDE.md
echo.
pause
