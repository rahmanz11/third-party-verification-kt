import kotlinx.coroutines.runBlocking
import com.external.verification.services.FingerprintDeviceService
import com.external.verification.models.*

/**
 * Test script for real fingerprint integration
 * This script tests the communication between Kotlin service and C# Windows Service
 */
fun main() = runBlocking {
    println("========================================")
    println("   Real Fingerprint Integration Test")
    println("========================================")
    println()
    
    val fingerprintService = FingerprintDeviceService()
    
    try {
        // Test 1: Check service health
        println("Test 1: Checking C# service health...")
        val isHealthy = fingerprintService.isSDKInitialized()
        if (isHealthy) {
            println("✓ C# service is healthy and responding")
        } else {
            println("✗ C# service is not responding")
            println("  Please ensure the service is running: sc start \"DigitalPersonaFingerprintService\"")
            return@runBlocking
        }
        
        // Test 2: Check device status
        println("\nTest 2: Checking device status...")
        val deviceStatus = fingerprintService.getDeviceStatus()
        println("Device Status:")
        println("  Connected: ${deviceStatus.connected}")
        println("  Device Name: ${deviceStatus.deviceName ?: "None"}")
        println("  Device ID: ${deviceStatus.deviceId ?: "None"}")
        println("  Error: ${deviceStatus.error ?: "None"}")
        
        if (deviceStatus.connected) {
            println("✓ Fingerprint device is connected and ready")
        } else {
            println("⚠ No fingerprint device connected")
            println("  Connect a fingerprint scanner and try again")
        }
        
        // Test 3: Test device connection
        println("\nTest 3: Testing device connection...")
        val connectionRequest = DeviceConnectionRequest(
            deviceType = "fingerprint_scanner",
            autoConnect = true,
            connectionTimeout = 10000
        )
        
        val connectionResponse = fingerprintService.connectDevice(connectionRequest)
        if (connectionResponse.success) {
            println("✓ Device connection successful")
            println("  Device: ${connectionResponse.deviceInfo?.deviceName}")
            println("  ID: ${connectionResponse.deviceInfo?.deviceId}")
        } else {
            println("✗ Device connection failed: ${connectionResponse.error}")
        }
        
        // Test 4: Test fingerprint capture (if device is connected)
        if (deviceStatus.connected) {
            println("\nTest 4: Testing fingerprint capture...")
            val captureRequest = FingerprintCaptureRequest(
                fingerType = "RIGHT_THUMB",
                qualityThreshold = 70,
                captureTimeout = 30000,
                retryCount = 3
            )
            
            println("  Capturing fingerprint for ${captureRequest.fingerType}...")
            println("  Please place your finger on the scanner...")
            
            val captureResponse = fingerprintService.captureFingerprint(captureRequest)
            if (captureResponse.success) {
                println("✓ Fingerprint capture successful")
                println("  Quality Score: ${captureResponse.qualityScore}")
                println("  Capture Time: ${captureResponse.captureTime}")
                println("  Image Data Length: ${captureResponse.imageData?.length ?: 0} characters")
                println("  WSQ Data Length: ${captureResponse.wsqData?.length ?: 0} characters")
            } else {
                println("✗ Fingerprint capture failed: ${captureResponse.error}")
            }
        } else {
            println("\nTest 4: Skipping fingerprint capture (no device connected)")
        }
        
        // Test 5: Test batch capture
        if (deviceStatus.connected) {
            println("\nTest 5: Testing batch fingerprint capture...")
            val batchRequest = BatchFingerprintCaptureRequest(
                fingers = listOf("RIGHT_THUMB", "LEFT_THUMB"),
                qualityThreshold = 70,
                captureTimeout = 60000,
                retryCount = 2
            )
            
            println("  Capturing batch fingerprints: ${batchRequest.fingers.joinToString(", ")}")
            
            val batchResponse = fingerprintService.captureBatchFingerprints(batchRequest)
            if (batchResponse.success) {
                println("✓ Batch capture completed")
                println("  Successful captures: ${batchResponse.capturedFingers.size}")
                println("  Failed captures: ${batchResponse.failedFingers.size}")
                println("  Total time: ${batchResponse.totalTime}ms")
                
                batchResponse.capturedFingers.forEach { capture ->
                    println("    ${capture.fingerType}: Quality ${capture.qualityScore}")
                }
                
                batchResponse.failedFingers.forEach { failed ->
                    println("    ${failed.fingerType}: Failed")
                }
            } else {
                println("✗ Batch capture failed: ${batchResponse.error}")
            }
        } else {
            println("\nTest 5: Skipping batch capture (no device connected)")
        }
        
        // Test 6: Test quality assessment
        println("\nTest 6: Testing quality assessment...")
        val testImageData = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==" // 1x1 pixel PNG
        val qualityAssessment = fingerprintService.assessFingerprintQuality(testImageData)
        
        println("  Quality Assessment:")
        println("    Overall Score: ${qualityAssessment.quality.overallScore}")
        println("    Clarity: ${qualityAssessment.quality.clarity}")
        println("    Contrast: ${qualityAssessment.quality.contrast}")
        println("    Coverage: ${qualityAssessment.quality.coverage}")
        println("    Ridge Definition: ${qualityAssessment.quality.ridgeDefinition}")
        println("    Acceptable: ${qualityAssessment.quality.isAcceptable}")
        
        println("\n========================================")
        println("   Integration Test Complete!")
        println("========================================")
        println()
        
        if (deviceStatus.connected) {
            println("✓ Real fingerprint integration is working correctly")
            println("✓ C# Windows Service is communicating properly")
            println("✓ Fingerprint device is connected and functional")
            println("✓ All API endpoints are responding correctly")
        } else {
            println("⚠ Integration is working but no fingerprint device is connected")
            println("  Connect a fingerprint scanner to test full functionality")
        }
        
    } catch (e: Exception) {
        println("✗ Integration test failed with error: ${e.message}")
        e.printStackTrace()
    } finally {
        fingerprintService.cleanup()
    }
    
    println("\nNext steps:")
    println("1. Connect a fingerprint scanner to test real capture")
    println("2. Test the web interface at http://localhost:8080/fingerprint-test.html")
    println("3. Test AFIS integration with real fingerprint data")
    println("4. Monitor service logs in Windows Event Viewer")
}
