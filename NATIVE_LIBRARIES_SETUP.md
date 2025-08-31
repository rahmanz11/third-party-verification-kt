# Native Libraries Setup for Digital Persona Fingerprint SDK

## Overview

This project requires specific native libraries (DLL files) to integrate with the Digital Persona fingerprint SDK. The main missing library is `otmcjni.dll` (One Touch for Microsoft Windows JNI).

## Required Libraries

### Critical Libraries (Required for Functionality)
- **`otmcjni.dll`** - One Touch for Microsoft Windows JNI (REQUIRED)
  - This is the core library that enables fingerprint device communication
  - Without this library, fingerprint functionality will not work

### Supporting Libraries
- **`DPJasPer.dll`** - Digital Persona JasPer library (for image processing)
- **`DPTSClnt.dll`** - Digital Persona TS Client library (for client operations)

## Current Status

✅ **Available Libraries:**
- `DPJasPer.dll` - Found in `native/` directory
- `DPTSClnt.dll` - Found in `native/` directory

❌ **Missing Libraries:**
- `otmcjni.dll` - **CRITICAL: This library is missing and required for fingerprint functionality**

## How to Obtain otmcjni.dll

### Option 1: Download from Digital Persona (Recommended)
1. Visit [Digital Persona Developer Portal](https://www.digitalpersona.com/developers/)
2. Download the "One Touch for Microsoft Windows SDK"
3. Install the SDK on your system
4. Locate `otmcjni.dll` in the SDK installation directory (typically `C:\Program Files\DigitalPersona\One Touch for Microsoft Windows SDK\bin\`)
5. Copy `otmcjni.dll` to the `native/` directory of this project

### Option 2: Extract from Existing Installation
If you already have Digital Persona software installed:
1. Search your system for `otmcjni.dll`
2. Common locations:
   - `C:\Program Files\DigitalPersona\`
   - `C:\Program Files (x86)\DigitalPersona\`
   - `C:\Windows\System32\`
3. Copy the found `otmcjni.dll` to the `native/` directory

### Option 3: Contact Digital Persona Support
If you cannot locate the library:
1. Contact Digital Persona technical support
2. Request the One Touch for Microsoft Windows SDK
3. Specify that you need the `otmcjni.dll` library

## Installation Steps

1. **Obtain the missing library:**
   - Download from Digital Persona developer portal, or
   - Extract from existing installation, or
   - Contact Digital Persona support

2. **Place the library in the correct location:**
   ```
   project-root/
   ├── native/
   │   ├── otmcjni.dll          ← Place the missing library here
   │   ├── DPJasPer.dll         ← Already present
   │   └── DPTSClnt.dll         ← Already present
   └── ...
   ```

3. **Verify the installation:**
   - Run the application
   - Check the SDK status endpoint: `GET /fingerprint/device/sdk-status`
   - Look for `"nativeLibrariesAvailable": true`

## Verification

After placing `otmcjni.dll` in the `native/` directory, you can verify the setup:

### Check SDK Status
```bash
curl -X GET "http://localhost:8080/fingerprint/device/sdk-status"
```

### Expected Response
```json
{
  "sdkInitialized": true,
  "nativeLibrariesAvailable": true,
  "missingNativeLibraries": [],
  "nativeDllExists": true,
  "missingLibrariesGuidance": {}
}
```

### If Libraries Are Still Missing
```json
{
  "sdkInitialized": false,
  "nativeLibrariesAvailable": false,
  "missingNativeLibraries": ["otmcjni.dll"],
  "nativeDllExists": false,
  "missingLibrariesGuidance": {
    "otmcjni.dll": "CRITICAL: otmcjni.dll (One Touch for Microsoft Windows JNI) is missing!..."
  }
}
```

## Troubleshooting

### Common Issues

1. **Library not found error:**
   - Ensure `otmcjni.dll` is in the `native/` directory
   - Check file permissions (should be readable by the application)
   - Verify the library is 64-bit if running on 64-bit Windows

2. **UnsatisfiedLinkError:**
   - This typically means the native library cannot be loaded
   - Check if the library is compatible with your Windows version
   - Ensure all dependencies are present

3. **SDK initialization failure:**
   - Check the application logs for detailed error messages
   - Verify all required libraries are present
   - Check the SDK status endpoint for guidance

### Debug Information

The application provides detailed debugging information through:
- **Logs**: Check application logs for detailed error messages
- **SDK Status Endpoint**: `GET /fingerprint/device/sdk-status`
- **Native Library Guidance**: `getNativeLibraryGuidance()` method

## Support

If you continue to experience issues:

1. **Check the logs** for detailed error messages
2. **Verify library versions** match your Digital Persona SDK version
3. **Contact Digital Persona support** for SDK-specific issues
4. **Check the application's SDK status** for detailed diagnostics

## Additional Resources

- [Digital Persona Developer Portal](https://www.digitalpersona.com/developers/)
- [One Touch for Microsoft Windows SDK Documentation](https://www.digitalpersona.com/developers/one-touch-for-microsoft-windows/)
- [Fingerprint Integration Documentation](FINGERPRINT_INTEGRATION.md)

---

**Note:** The `otmcjni.dll` library is proprietary software from Digital Persona and must be obtained through legitimate channels. This project cannot distribute this library due to licensing restrictions.
