# Digital Persona Fingerprint Service

This is a Windows service that provides fingerprint scanner integration using the Digital Persona .NET SDK. It exposes a REST API that can be consumed by other applications (like the Kotlin verification service) to perform fingerprint operations.

## Features

- **Device Management**: Connect/disconnect to fingerprint scanners
- **Fingerprint Capture**: Single and batch fingerprint capture
- **Quality Assessment**: Automatic quality evaluation of captured fingerprints
- **WSQ Conversion**: Convert captured images to WSQ format for AFIS compatibility
- **REST API**: HTTP endpoints for easy integration
- **Windows Service**: Runs as a background service with automatic startup

## Prerequisites

1. **Windows 10/11** (64-bit)
2. **.NET 8.0 Runtime** or **.NET 8.0 SDK**
3. **Digital Persona One Touch for Windows SDK** installed
4. **Administrator privileges** for service installation

## Installation

### 1. Build the Service

```bash
cd DigitalPersonaService
dotnet build -c Release
```

### 2. Install as Windows Service

Run the installation script as administrator:

```bash
install-service.bat
```

Or manually install using:

```bash
# Install the service
sc create "DigitalPersonaFingerprintService" binPath="C:\path\to\DigitalPersonaService.exe" start=auto DisplayName="Digital Persona Fingerprint Service"

# Start the service
sc start "DigitalPersonaFingerprintService"
```

### 3. Verify Installation

Check service status:

```bash
sc query "DigitalPersonaFingerprintService"
```

## Configuration

The service configuration is in `appsettings.json`:

```json
{
  "ServiceSettings": {
    "Port": 5001,
    "ServiceName": "DigitalPersonaFingerprintService"
  },
  "DigitalPersona": {
    "SDKPath": "C:\\Program Files\\DigitalPersona",
    "DeviceTimeout": 30000,
    "QualityThreshold": 70
  }
}
```

## API Endpoints

### Health Check
```
GET /api/fingerprint/health
```

### Device Management
```
GET  /api/fingerprint/device/status      - Get device status
POST /api/fingerprint/device/connect     - Connect to device
POST /api/fingerprint/device/disconnect  - Disconnect from device
```

### Fingerprint Capture
```
POST /api/fingerprint/capture             - Capture single fingerprint
POST /api/fingerprint/capture/batch       - Batch capture multiple fingers
POST /api/fingerprint/capture/{type}/cancel - Cancel active capture
```

### Quality & Utilities
```
POST /api/fingerprint/quality/assess     - Assess fingerprint quality
GET  /api/fingerprint/sdk/version        - Get SDK version
```

## Usage Examples

### Connect to Device
```bash
curl -X POST http://localhost:5001/api/fingerprint/device/connect
```

### Capture Fingerprint
```bash
curl -X POST http://localhost:5001/api/fingerprint/capture \
  -H "Content-Type: application/json" \
  -d '{
    "fingerType": "LEFT_THUMB",
    "qualityThreshold": 70,
    "timeoutMs": 30000,
    "convertToWsq": true
  }'
```

### Batch Capture
```bash
curl -X POST http://localhost:5001/api/fingerprint/capture/batch \
  -H "Content-Type: application/json" \
  -d '{
    "fingerTypes": ["LEFT_THUMB", "RIGHT_THUMB", "LEFT_INDEX"],
    "qualityThreshold": 70,
    "timeoutMs": 60000
  }'
```

## Integration with Kotlin Service

The Kotlin application can communicate with this service via HTTP calls. Update the `FingerprintDeviceService.kt` to make HTTP requests to the C# service instead of trying to use the Java SDK directly.

## Troubleshooting

### Service Won't Start
1. Check Windows Event Viewer for error messages
2. Verify .NET 8.0 is installed
3. Ensure Digital Persona SDK is properly installed
4. Check if port 5001 is available

### Device Connection Issues
1. Verify fingerprint scanner is connected and powered on
2. Check device drivers are installed
3. Ensure Digital Persona SDK is accessible
4. Review service logs

### Build Errors
1. Ensure .NET 8.0 SDK is installed
2. Check all NuGet packages are restored
3. Verify project file syntax

## Development Mode

Run the service in console mode for development:

```bash
dotnet run -- --console
```

This will start the service as a console application instead of a Windows service.

## Logging

Logs are written to:
- **Console**: When running in console mode
- **Windows Event Log**: When running as a service
- **File**: Configured in `appsettings.json`

## Security Considerations

- The service runs with local system privileges
- API endpoints are not authenticated by default
- Consider adding authentication for production use
- Restrict network access if needed

## Support

For issues related to:
- **Service installation**: Check Windows Event Viewer and service logs
- **Device connectivity**: Verify Digital Persona SDK installation
- **API integration**: Review HTTP response codes and error messages

## Next Steps

1. **Integrate Digital Persona .NET SDK**: Add actual SDK references and implementation
2. **Add authentication**: Implement API key or JWT authentication
3. **Enhanced error handling**: Add retry logic and better error reporting
4. **Performance optimization**: Implement connection pooling and caching
5. **Monitoring**: Add metrics collection and health monitoring
