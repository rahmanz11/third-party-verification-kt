package com.external.verification.models

import kotlinx.serialization.Serializable

// Fingerprint Device Status
@Serializable
data class FingerprintDeviceStatus(
    val connected: Boolean,
    val deviceName: String? = null,
    val deviceId: String? = null,
    val error: String? = null
)

// Fingerprint Capture Request
@Serializable
data class FingerprintCaptureRequest(
    val fingerType: String, // e.g., "RIGHT_THUMB", "LEFT_INDEX"
    val qualityThreshold: Int = 50, // Minimum quality score (0-100)
    val captureTimeout: Int = 30000, // Timeout in milliseconds
    val retryCount: Int = 3
)

// Fingerprint Capture Response
@Serializable
data class FingerprintCaptureResponse(
    val success: Boolean,
    val fingerType: String,
    val imageData: String? = null, // Base64 encoded image
    val wsqData: String? = null, // Base64 encoded WSQ data
    val qualityScore: Int? = null,
    val captureTime: String? = null,
    val error: String? = null
)

// Fingerprint Quality Assessment
@Serializable
data class FingerprintQuality(
    val overallScore: Int, // 0-100
    val clarity: Int, // 0-100
    val contrast: Int, // 0-100
    val coverage: Int, // 0-100
    val ridgeDefinition: Int, // 0-100
    val isAcceptable: Boolean
)

// Device Connection Request
@Serializable
data class DeviceConnectionRequest(
    val deviceType: String = "fingerprint_scanner",
    val autoConnect: Boolean = true,
    val connectionTimeout: Int = 10000
)

// Device Connection Response
@Serializable
data class DeviceConnectionResponse(
    val success: Boolean,
    val deviceInfo: DeviceInfo? = null,
    val error: String? = null
)

// Device Information
@Serializable
data class DeviceInfo(
    val deviceId: String,
    val deviceName: String,
    val deviceType: String,
    val firmwareVersion: String? = null,
    val serialNumber: String? = null,
    val capabilities: List<String> = emptyList()
)

// WebSocket Messages for Real-time Communication
@Serializable
sealed class FingerprintWebSocketMessage {
    @Serializable
    data class DeviceConnected(val deviceInfo: DeviceInfo) : FingerprintWebSocketMessage()
    
    @Serializable
    data class DeviceDisconnected(val deviceId: String) : FingerprintWebSocketMessage()
    
    @Serializable
    data class CaptureStarted(val fingerType: String) : FingerprintWebSocketMessage()
    
    @Serializable
    data class CaptureProgress(val fingerType: String, val progress: Int) : FingerprintWebSocketMessage()
    
    @Serializable
    data class CaptureCompleted(val response: FingerprintCaptureResponse) : FingerprintWebSocketMessage()
    
    @Serializable
    data class CaptureError(val fingerType: String, val error: String) : FingerprintWebSocketMessage()
    
    @Serializable
    data class QualityAssessment(val fingerType: String, val quality: FingerprintQuality) : FingerprintWebSocketMessage()
}

// Fingerprint Template Data
@Serializable
data class FingerprintTemplate(
    val fingerType: String,
    val templateData: String, // Base64 encoded template
    val qualityScore: Int,
    val captureTime: String,
    val deviceId: String
)

// Batch Fingerprint Capture Request
@Serializable
data class BatchFingerprintCaptureRequest(
    val fingers: List<String>, // List of finger types to capture
    val qualityThreshold: Int = 50,
    val captureTimeout: Int = 30000,
    val retryCount: Int = 3
)

// Batch Fingerprint Capture Response
@Serializable
data class BatchFingerprintCaptureResponse(
    val success: Boolean,
    val capturedFingers: List<FingerprintCaptureResponse> = emptyList(),
    val failedFingers: List<FailedFingerCapture> = emptyList(),
    val totalTime: Long? = null, // Total capture time in milliseconds
    val error: String? = null
)

// Failed Finger Capture
@Serializable
data class FailedFingerCapture(
    val fingerType: String,
    val error: String,
    val retryCount: Int
)

// Response models for device status endpoints
@Serializable
data class DeviceStatusResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class DeviceLockStatusResponse(
    val locked: Boolean
)
