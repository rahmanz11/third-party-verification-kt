@echo off
echo Digital Persona Fingerprint Service Installation
echo ===============================================

REM Check if running as administrator
net session >nul 2>&1
if %errorLevel% == 0 (
    echo Running as administrator - proceeding with installation
) else (
    echo ERROR: This script must be run as administrator
    echo Right-click and select "Run as administrator"
    pause
    exit /b 1
)

REM Build the service
echo Building the service...
dotnet build -c Release

if %errorLevel% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

REM Install the service
echo Installing the service...
sc create "DigitalPersonaFingerprintService" binPath="%~dp0bin\Release\net8.0\DigitalPersonaService.exe" start=auto DisplayName="Digital Persona Fingerprint Service"

if %errorLevel% == 0 (
    echo Service installed successfully
    echo Starting the service...
    sc start "DigitalPersonaFingerprintService"
    
    if %errorLevel% == 0 (
        echo Service started successfully
        echo.
        echo Service Status:
        sc query "DigitalPersonaFingerprintService"
    ) else (
        echo ERROR: Failed to start service
    )
) else (
    echo ERROR: Failed to install service
)

echo.
echo Installation complete. The service will start automatically on system boot.
echo.
echo To manage the service, use:
echo   sc start "DigitalPersonaFingerprintService"    - Start the service
echo   sc stop "DigitalPersonaFingerprintService"     - Stop the service
echo   sc delete "DigitalPersonaFingerprintService"   - Remove the service
echo.
pause
