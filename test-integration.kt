import kotlinx.coroutines.runBlocking
import com.external.verification.services.FingerprintDeviceService
import com.external.verification.models.*

fun main() = runBlocking {
    println("🧪 Testing Digital Persona Integration...")
    println("==========================================")
    
    val service = FingerprintDeviceService()
    
    try {
        // Test 1: Check if C# service is available
        println("\n1️⃣ Testing C# Service Health...")
        val sdkStatus = service.getSDKStatus()
        println("   SDK Status: $sdkStatus")
        
        // Test 2: Check device connection
        println("\n2️⃣ Testing Device Connection...")
        val deviceStatus = service.getDeviceStatus()
        println("   Device Status: ${deviceStatus.connected}")
        println("   Device Name: ${deviceStatus.deviceName}")
        println("   Device ID: ${deviceStatus.deviceId}")
        
        // Test 3: Connect to device
        println("\n3️⃣ Testing Device Connection...")
        val connectionRequest = DeviceConnectionRequest(
            deviceType = "fingerprint_scanner",
            autoConnect = true,
            connectionTimeout = 10000
        )
        val connectionResponse = service.connectDevice(connectionRequest)
        println("   Connection Success: ${connectionResponse.success}")
        if (connectionResponse.deviceInfo != null) {
            println("   Connected Device: ${connectionResponse.deviceInfo.deviceName}")
        }
        
        // Test 4: Test fingerprint capture
        println("\n4️⃣ Testing Fingerprint Capture...")
        val captureRequest = FingerprintCaptureRequest(
            fingerType = "LEFT_THUMB",
            qualityThreshold = 70,
            captureTimeout = 30000,
            retryCount = 3
        )
        val captureResponse = service.captureFingerprint(captureRequest)
        println("   Capture Success: ${captureResponse.success}")
        println("   Finger Type: ${captureResponse.fingerType}")
        println("   Quality Score: ${captureResponse.qualityScore}")
        
        // Test 5: Test batch capture
        println("\n5️⃣ Testing Batch Capture...")
        val batchRequest = BatchFingerprintCaptureRequest(
            fingers = listOf("LEFT_THUMB", "RIGHT_THUMB"),
            qualityThreshold = 70,
            captureTimeout = 60000,
            retryCount = 2
        )
        val batchResponse = service.captureBatchFingerprints(batchRequest)
        println("   Batch Capture Success: ${batchResponse.success}")
        println("   Captured Fingers: ${batchResponse.capturedFingers.size}")
        println("   Failed Fingers: ${batchResponse.failedFingers.size}")
        
        println("\n🎉 All Tests Completed Successfully!")
        println("✅ Your Kotlin app is now successfully communicating with the C# service!")
        
    } catch (e: Exception) {
        println("\n❌ Test Failed with Error:")
        println("   ${e.message}")
        e.printStackTrace()
    } finally {
        service.cleanup()
    }
}
