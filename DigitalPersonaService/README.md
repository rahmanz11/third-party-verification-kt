# Digital Persona Fingerprint Service

A Windows service that provides HTTP API access to Digital Persona fingerprint scanner functionality.

## Overview

This service acts as a bridge between the Kotlin verification application and Digital Persona fingerprint scanners, providing a REST API for fingerprint capture operations.

## Features

- **HTTP API**: RESTful endpoints for fingerprint operations
- **Windows Service**: Runs as a Windows service with automatic startup
- **Device Management**: Automatic detection and connection to fingerprint scanners
- **Quality Assessment**: Fingerprint quality evaluation
- **Batch Operations**: Capture multiple fingerprints in sequence
- **Comprehensive Logging**: Detailed logging with Serilog

## Prerequisites

### System Requirements
- **Windows 10/11** (64-bit)
- **.NET 8.0 Runtime** or **.NET 8.0 SDK**
- **Digital Persona One Touch for Windows SDK** installed
- **Administrator privileges** for service installation

### SDK Installation
1. Install Digital Persona SDK from the official source
2. Verify installation in `C:\Program Files\DigitalPersona`
3. Copy required DLL files to the `SDK\Bin\` directory

## Quick Start

### 1. Build the Service
```bash
cd DigitalPersonaService
dotnet build -c Release
```

### 2. Install as Windows Service
```bash
# Run as administrator
install-service.bat
```

### 3. Test the API
```bash
# Health check
curl http://localhost:5001/api/fingerprint/health

# Device status
curl http://localhost:5001/api/fingerprint/device/status
```

## API Endpoints

### Health and Status
- `GET /api/fingerprint/health` - Service health check
- `GET /api/fingerprint/device/status` - Device connection status
- `GET /api/fingerprint/sdk/version` - SDK version information

### Device Management
- `POST /api/fingerprint/device/connect` - Connect to fingerprint device
- `POST /api/fingerprint/device/disconnect` - Disconnect from device

### Fingerprint Capture
- `POST /api/fingerprint/capture` - Capture single fingerprint
- `POST /api/fingerprint/capture/batch` - Batch capture multiple fingers
- `POST /api/fingerprint/capture/{type}/cancel` - Cancel active capture

### Quality Assessment
- `POST /api/fingerprint/quality/assess` - Assess fingerprint quality

## Configuration

### appsettings.json
```json
{
  "ServiceSettings": {
    "Port": 5001,
    "ServiceName": "DigitalPersonaFingerprintService"
  },
  "DigitalPersona": {
    "SDKPath": "C:\\Program Files\\DigitalPersona",
    "DeviceTimeout": 30000,
    "QualityThreshold": 70,
    "MaxRetries": 3,
    "CaptureTimeout": 60000
  }
}
```

## Development

### Running in Development Mode
```bash
dotnet run -- --console
```

### Building for Production
```bash
dotnet build -c Release
dotnet publish -c Release -o publish
```

## Service Management

### Windows Service Commands
```bash
# Check service status
sc query "DigitalPersonaFingerprintService"

# Start service
sc start "DigitalPersonaFingerprintService"

# Stop service
sc stop "DigitalPersonaFingerprintService"

# Remove service
sc delete "DigitalPersonaFingerprintService"
```

### Logs
- **Console**: Real-time logging when running in console mode
- **File**: `logs/digitalpersona-service-YYYY-MM-DD.log`
- **Event Log**: Windows Event Viewer under "DigitalPersonaFingerprintService"

## Troubleshooting

### Common Issues

#### 1. Service Won't Start
- Check .NET 8.0 installation
- Verify port 5001 is not in use
- Check Windows Event Viewer for error messages
- Ensure Digital Persona SDK is properly installed

#### 2. Device Connection Issues
- Verify fingerprint scanner is connected and powered
- Check device drivers are installed
- Ensure SDK path in configuration is correct
- Check service has access to device (run as administrator)

#### 3. API Communication Issues
- Verify service is running on port 5001
- Check firewall settings
- Test with curl or Postman
- Review service logs for errors

### Debug Mode
Enable debug logging in `appsettings.json`:
```json
{
  "Logging": {
    "LogLevel": {
      "DigitalPersonaService": "Debug"
    }
  }
}
```

## Security Considerations

### Current Implementation
- **Local Only**: Service runs on localhost only
- **No Authentication**: API endpoints are not authenticated
- **System Privileges**: Service runs with local system privileges

### Production Recommendations
- **Add Authentication**: Implement API key or JWT authentication
- **Network Security**: Restrict access to authorized networks only
- **Audit Logging**: Log all fingerprint operations for compliance
- **Data Encryption**: Encrypt sensitive data in transit and at rest

## Performance Optimization

### Current Optimizations
- **Async Operations**: Non-blocking async/await operations
- **Connection Pooling**: Efficient HTTP client usage
- **Timeout Management**: Configurable timeouts for different operations
- **Error Handling**: Graceful degradation and retry logic

### Future Enhancements
- **Caching**: Cache device status and SDK information
- **Batch Operations**: Optimize batch fingerprint capture
- **Connection Persistence**: Maintain persistent device connections
- **Load Balancing**: Support for multiple fingerprint devices

## Integration with Kotlin Application

The Kotlin application communicates with this service via HTTP API. The `FingerprintDeviceService.kt` has been updated to:

1. **Make HTTP Requests**: Uses Ktor HTTP client for API communication
2. **Handle Async Operations**: All methods are suspend functions
3. **Map Responses**: Converts C# service responses to Kotlin models
4. **Provide Fallbacks**: Graceful degradation when service is unavailable

## Support

### Documentation
- **API Reference**: Service endpoints and request/response formats
- **Integration Guide**: Kotlin application integration
- **Troubleshooting**: Common issues and solutions

### Development Resources
- **Digital Persona SDK**: Official SDK documentation
- **.NET Documentation**: Microsoft .NET development resources
- **Serilog Documentation**: Logging framework documentation

## License

This service is part of the Third-Party Verification System and follows the same licensing terms.
