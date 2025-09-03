package com.external.verification.services

import com.external.verification.models.*
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.io.ByteArrayOutputStream
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.util.Base64
import java.io.File
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable

/**
 * Service for managing fingerprint device operations
 * 
 * This service communicates with the Digital Persona C# Windows Service via HTTP API
 * to provide fingerprint scanner functionality without requiring Java SDK dependencies.
 */
class FingerprintDeviceService {
    
    private val logger = KotlinLogging.logger {}
    
    // C# Service configuration
    private val csharpServiceUrl = "http://localhost:5001"
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = Logger.DEFAULT
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 30000
        }
    }
    
    // Device connection state
    private val deviceConnected = AtomicBoolean(false)
    private val currentDevice = ConcurrentHashMap<String, DeviceInfo>()
    
    // Capture state management
    private val activeCaptures = ConcurrentHashMap<String, String>()
    private val captureCounter = AtomicInteger(0)
    
    // Real-time capture state
    private var captureInProgress = AtomicBoolean(false)
    
    // Quality assessment thresholds
    private val qualityThresholds = mapOf(
        "clarity" to 60,
        "contrast" to 50,
        "coverage" to 70,
        "ridgeDefinition" to 65
    )
    
    init {
        logger.info { "FingerprintDeviceService initialized - communicating with C# Windows Service at $csharpServiceUrl" }
        // Check if C# service is available - launch in coroutine
        CoroutineScope(Dispatchers.IO).launch {
            checkCSharpServiceHealth()
        }
    }
    
    // Data classes for C# service communication
    @Serializable
    data class CSharpServiceHealth(
        val status: String,
        val timestamp: String,
        val sdkVersion: String,
        val deviceConnected: Boolean
    )
    
    @Serializable
    data class CSharpDeviceStatus(
        val connected: Boolean,
        val deviceName: String?,
        val deviceId: String?,
        val firmwareVersion: String?,
        val error: String?,
        val timestamp: String
    )
    
    @Serializable
    data class CSharpDeviceConnection(
        val success: Boolean,
        val deviceInfo: CSharpDeviceStatus?,
        val error: String?
    )
    
    @Serializable
    data class CSharpFingerprintCapture(
        val success: Boolean,
        val fingerType: String,
        val imageData: String?,
        val wsqData: String?,
        val qualityScore: Int?,
        val captureTime: String?,
        val error: String?
    )
    
    @Serializable
    data class CSharpQualityAssessment(
        val success: Boolean,
        val overallScore: Int,
        val clarity: Int,
        val contrast: Int,
        val coverage: Int,
        val ridgeDefinition: Int,
        val isAcceptable: Boolean,
        val error: String?
    )
    
    @Serializable
    data class CSharpCaptureRequest(
        val fingerType: String,
        val qualityThreshold: Int
    )
    
    @Serializable
    data class CSharpBatchCaptureRequest(
        val fingerTypes: List<String>,
        val qualityThreshold: Int
    )
    
    /**
     * Check if the C# Windows Service is available and healthy
     */
    private suspend fun checkCSharpServiceHealth() {
        try {
            val response = httpClient.get("$csharpServiceUrl/api/fingerprint/health")
            if (response.status.isSuccess()) {
                val health = response.body<CSharpServiceHealth>()
                logger.info { "C# Service is healthy: ${health.status}, SDK: ${health.sdkVersion}" }
                deviceConnected.set(health.deviceConnected)
            } else {
                logger.warn { "C# Service health check failed with status: ${response.status}" }
            }
        } catch (e: Exception) {
            logger.warn { "C# Service is not available: ${e.message}" }
            logger.info { "Please ensure the Digital Persona C# Windows Service is running on port 5001" }
        }
    }
    
    /**
     * Check if any fingerprint devices are connected
     */
    suspend fun areDevicesConnected(): Boolean {
        return try {
            val response = httpClient.get("$csharpServiceUrl/api/fingerprint/device/status")
            if (response.status.isSuccess()) {
                val status = response.body<CSharpDeviceStatus>()
                status.connected
            } else {
                false
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to check device connection status" }
            false
        }
    }
    
    /**
     * Get list of connected fingerprint devices
     */
    suspend fun getConnectedDevices(): List<DeviceInfo> {
        return try {
            val response = httpClient.get("$csharpServiceUrl/api/fingerprint/device/status")
            if (response.status.isSuccess()) {
                val status = response.body<CSharpDeviceStatus>()
                if (status.connected && status.deviceName != null && status.deviceId != null) {
                    listOf(DeviceInfo(
                        deviceId = status.deviceId,
                        deviceName = status.deviceName,
                        deviceType = "fingerprint_scanner",
                        firmwareVersion = status.firmwareVersion ?: "Unknown"
                    ))
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to get connected devices" }
            emptyList()
        }
    }
    
    /**
     * Initialize fingerprint capture for a device
     */
    suspend fun initializeCapture(deviceId: String): Boolean {
        return try {
            val response = httpClient.post("$csharpServiceUrl/api/fingerprint/device/connect")
            if (response.status.isSuccess()) {
                val connection = response.body<CSharpDeviceConnection>()
                connection.success
            } else {
                false
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to initialize capture for device $deviceId" }
            false
        }
    }
    
    /**
     * Start fingerprint capture
     */
    suspend fun startCapture(deviceId: String): Boolean {
        // Capture is started when calling the capture endpoint
        return true
    }
    
    /**
     * Stop fingerprint capture
     */
    suspend fun stopCapture(deviceId: String): Boolean {
        return true
    }
    
    /**
     * Get captured fingerprint sample
     */
    suspend fun getCapturedSample(deviceId: String): FingerprintCaptureResponse? {
        // This would need to be implemented based on the specific capture flow
        return null
    }
    
    /**
     * Assess fingerprint quality
     */
    suspend fun assessQuality(sample: FingerprintCaptureResponse): FingerprintWebSocketMessage.QualityAssessment {
        return try {
            val requestBody = mapOf("imageData" to (sample.imageData ?: ""))
            val response = httpClient.post("$csharpServiceUrl/api/fingerprint/quality/assess") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            if (response.status.isSuccess()) {
                val quality = response.body<CSharpQualityAssessment>()
                FingerprintWebSocketMessage.QualityAssessment(
                    fingerType = sample.fingerType,
                    quality = FingerprintQuality(
                        overallScore = quality.overallScore,
                        clarity = quality.clarity,
                        contrast = quality.contrast,
                        coverage = quality.coverage,
                        ridgeDefinition = quality.ridgeDefinition,
                        isAcceptable = quality.isAcceptable
                    )
                )
            } else {
                FingerprintWebSocketMessage.QualityAssessment(
                    fingerType = sample.fingerType,
                    quality = FingerprintQuality(
                        overallScore = 0,
                        clarity = 0,
                        contrast = 0,
                        coverage = 0,
                        ridgeDefinition = 0,
                        isAcceptable = false
                    )
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to assess fingerprint quality" }
            FingerprintWebSocketMessage.QualityAssessment(
                fingerType = sample.fingerType,
                quality = FingerprintQuality(
                    overallScore = 0,
                    clarity = 0,
                    contrast = 0,
                    coverage = 0,
                    ridgeDefinition = 0,
                    isAcceptable = false
                )
            )
        }
    }
    
    /**
     * Get SDK status
     */
    suspend fun getSDKStatus(): String {
        return try {
            val response = httpClient.get("$csharpServiceUrl/api/fingerprint/sdk/version")
            if (response.status.isSuccess()) {
                val version = response.body<Map<String, String>>()
                version["version"] ?: "Unknown"
            } else {
                "C# Service unavailable"
            }
        } catch (e: Exception) {
            "C# Service unavailable: ${e.message}"
        }
    }
    
    /**
     * Check if SDK is initialized
     */
    suspend fun isSDKInitialized(): Boolean {
        return try {
            val response = httpClient.get("$csharpServiceUrl/api/fingerprint/health")
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get missing native libraries
     */
    fun getMissingNativeLibraries(): List<String> {
        return listOf("Digital Persona .NET SDK", "C# Windows Service")
    }
    
    /**
     * Check if native libraries are available
     */
    suspend fun areNativeLibrariesAvailable(): Boolean {
        return isSDKInitialized()
    }
    
    /**
     * Check if full SDK is available
     */
    suspend fun isFullSDKAvailable(): Boolean {
        return isSDKInitialized()
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        logger.info { "FingerprintDeviceService cleanup completed" }
        httpClient.close()
    }
    
    // Additional methods that the routes are trying to call
    suspend fun connectDevice(request: DeviceConnectionRequest): DeviceConnectionResponse {
        return try {
            val response = httpClient.post("$csharpServiceUrl/api/fingerprint/device/connect")
            if (response.status.isSuccess()) {
                val connection = response.body<CSharpDeviceConnection>()
                if (connection.success && connection.deviceInfo != null) {
                    DeviceConnectionResponse(
                        success = true,
                        deviceInfo =                     DeviceInfo(
                        deviceId = connection.deviceInfo.deviceId ?: "",
                        deviceName = connection.deviceInfo.deviceName ?: "",
                        deviceType = "fingerprint_scanner",
                        firmwareVersion = connection.deviceInfo.firmwareVersion ?: "Unknown"
                    ),
                        error = null
                    )
                } else {
                    DeviceConnectionResponse(
                        success = false,
                        deviceInfo = null,
                        error = connection.error ?: "Unknown error"
                    )
                }
            } else {
                DeviceConnectionResponse(
                    success = false,
                    deviceInfo = null,
                    error = "HTTP ${response.status.value}"
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to connect device" }
            DeviceConnectionResponse(
                success = false,
                deviceInfo = null,
                error = e.message ?: "Unknown error"
            )
        }
    }
    
    suspend fun disconnectDevice(): Boolean {
        return try {
            val response = httpClient.post("$csharpServiceUrl/api/fingerprint/device/disconnect")
            if (response.status.isSuccess()) {
                val result = response.body<Map<String, Boolean>>()
                result["success"] ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to disconnect device" }
            false
        }
    }
    
    suspend fun getDeviceStatus(): FingerprintDeviceStatus {
        return try {
            val response = httpClient.get("$csharpServiceUrl/api/fingerprint/device/status")
            if (response.status.isSuccess()) {
                val status = response.body<CSharpDeviceStatus>()
                FingerprintDeviceStatus(
                    connected = status.connected,
                    deviceName = status.deviceName,
                    deviceId = status.deviceId,
                    error = status.error
                )
            } else {
                FingerprintDeviceStatus(
                    connected = false,
                    deviceName = null,
                    deviceId = null,
                    error = "HTTP ${response.status.value}"
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to get device status" }
            FingerprintDeviceStatus(
                connected = false,
                deviceName = null,
                deviceId = null,
                error = e.message ?: "Unknown error"
            )
        }
    }
    
    suspend fun refreshDeviceStatus(): String {
        return try {
            val response = httpClient.get("$csharpServiceUrl/api/fingerprint/device/status")
            if (response.status.isSuccess()) {
                val status = response.body<CSharpDeviceStatus>()
                if (status.connected) "CONNECTED" else "DISCONNECTED"
            } else {
                "ERROR"
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to refresh device status" }
            "ERROR"
        }
    }
    
    suspend fun lockDeviceStatus(): String {
        // Device locking is not implemented in the C# service yet
        return "NOT_IMPLEMENTED"
    }
    
    suspend fun isDeviceLocked(): Boolean {
        // Device locking is not implemented in the C# service yet
        return false
    }
    
    suspend fun reinitializeSDK(): Boolean {
        return try {
            val response = httpClient.get("$csharpServiceUrl/api/fingerprint/health")
            response.status.isSuccess()
        } catch (e: Exception) {
            logger.error(e) { "Failed to reinitialize SDK" }
            false
        }
    }
    
    suspend fun captureFingerprint(request: FingerprintCaptureRequest): FingerprintCaptureResponse {
        return try {
            val requestBody = CSharpCaptureRequest(
                fingerType = request.fingerType,
                qualityThreshold = request.qualityThreshold
            )
            
            val response = httpClient.post("$csharpServiceUrl/api/fingerprint/capture") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            if (response.status.isSuccess()) {
                val capture = response.body<CSharpFingerprintCapture>()
                if (capture.success) {
                    FingerprintCaptureResponse(
                        success = true,
                        fingerType = capture.fingerType,
                        imageData = capture.imageData,
                        wsqData = capture.wsqData,
                        qualityScore = capture.qualityScore,
                                                    captureTime = capture.captureTime,
                        error = null
                    )
                } else {
                    FingerprintCaptureResponse(
                        success = false,
                        fingerType = capture.fingerType,
                        imageData = null,
                        wsqData = null,
                        qualityScore = null,
                        captureTime = null,
                        error = capture.error ?: "Unknown error"
                    )
                }
            } else {
                FingerprintCaptureResponse(
                    success = false,
                    fingerType = request.fingerType,
                    imageData = null,
                    wsqData = null,
                    qualityScore = null,
                    captureTime = null,
                    error = "HTTP ${response.status.value}"
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to capture fingerprint" }
            FingerprintCaptureResponse(
                success = false,
                fingerType = request.fingerType,
                imageData = null,
                wsqData = null,
                qualityScore = null,
                captureTime = null,
                    error = e.message ?: "Unknown error"
            )
        }
    }
    
    suspend fun captureBatchFingerprints(request: BatchFingerprintCaptureRequest): BatchFingerprintCaptureResponse {
        return try {
            val requestBody = CSharpBatchCaptureRequest(
                fingerTypes = request.fingers,
                qualityThreshold = request.qualityThreshold
            )
            
            val response = httpClient.post("$csharpServiceUrl/api/fingerprint/capture/batch") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            if (response.status.isSuccess()) {
                val batch = response.body<Map<String, Any>>()
                if (batch["success"] == true) {
                    val capturedFingers = (batch["capturedFingers"] as? List<Map<String, Any>>)?.map { finger ->
                        FingerprintCaptureResponse(
                            success = finger["success"] as? Boolean ?: false,
                            fingerType = finger["fingerType"] as? String ?: "",
                            imageData = finger["imageData"] as? String,
                            wsqData = finger["wsqData"] as? String,
                            qualityScore = finger["qualityScore"] as? Int,
                            captureTime = finger["captureTime"] as? String,
                            error = finger["error"] as? String
                        )
                    } ?: emptyList()
                    
                    val failedFingers = (batch["failedFingers"] as? List<String>)?.map { fingerType ->
                        FailedFingerCapture(
                            fingerType = fingerType,
                            error = "Capture failed",
                            retryCount = 0
                        )
                    } ?: emptyList()
                    val totalTime = batch["totalTime"] as? String
                    
                    BatchFingerprintCaptureResponse(
                        success = true,
                        capturedFingers = capturedFingers,
                        failedFingers = failedFingers,
                        totalTime = totalTime?.let { parseDuration(it) }?.toMillis(),
                        error = null
                    )
                } else {
                    BatchFingerprintCaptureResponse(
                        success = false,
                        capturedFingers = emptyList(),
                        failedFingers = emptyList(),
                        totalTime = null,
                        error = batch["error"] as? String ?: "Unknown error"
                    )
                }
            } else {
                BatchFingerprintCaptureResponse(
                    success = false,
                    capturedFingers = emptyList(),
                    failedFingers = emptyList(),
                    totalTime = null,
                    error = "HTTP ${response.status.value}"
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to capture batch fingerprints" }
            BatchFingerprintCaptureResponse(
                success = false,
                capturedFingers = emptyList(),
                failedFingers = emptyList(),
                totalTime = null,
                error = e.message ?: "Unknown error"
            )
        }
    }
    
    suspend fun cancelCapture(fingerType: String): Boolean {
        return try {
            val response = httpClient.post("$csharpServiceUrl/api/fingerprint/capture/$fingerType/cancel")
            if (response.status.isSuccess()) {
                val result = response.body<Map<String, Boolean>>()
                result["success"] ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to cancel capture for $fingerType" }
            false
        }
    }
    
    suspend fun getActiveCaptures(): Map<String, String> {
        // This would need to be implemented in the C# service
        return emptyMap()
    }
    
    suspend fun assessFingerprintQuality(imageData: String): FingerprintWebSocketMessage.QualityAssessment {
        return try {
            val requestBody = mapOf("imageData" to imageData)
            val response = httpClient.post("$csharpServiceUrl/api/fingerprint/quality/assess") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            if (response.status.isSuccess()) {
                val quality = response.body<CSharpQualityAssessment>()
                FingerprintWebSocketMessage.QualityAssessment(
                    fingerType = "UNKNOWN",
                    quality = FingerprintQuality(
                        overallScore = quality.overallScore,
                        clarity = quality.clarity,
                        contrast = quality.contrast,
                        coverage = quality.coverage,
                        ridgeDefinition = quality.ridgeDefinition,
                        isAcceptable = quality.isAcceptable
                    )
                )
            } else {
                FingerprintWebSocketMessage.QualityAssessment(
                    fingerType = "UNKNOWN",
                    quality = FingerprintQuality(
                        overallScore = 0,
                        clarity = 0,
                        contrast = 0,
                        coverage = 0,
                        ridgeDefinition = 0,
                        isAcceptable = false
                    )
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to assess fingerprint quality" }
            FingerprintWebSocketMessage.QualityAssessment(
                fingerType = "UNKNOWN",
                quality = FingerprintQuality(
                    overallScore = 0,
                    clarity = 0,
                    contrast = 0,
                    coverage = 0,
                    ridgeDefinition = 0,
                    isAcceptable = false
                )
            )
        }
    }
    
    /**
     * Helper function to parse duration string from C# service
     */
    private fun parseDuration(durationString: String): java.time.Duration? {
        return try {
            // Parse C# TimeSpan format (e.g., "00:00:05.1234567")
            val parts = durationString.split(":")
            if (parts.size >= 3) {
                val hours = parts[0].toLong()
                val minutes = parts[1].toLong()
                val seconds = parts[2].split(".")[0].toLong()
                val milliseconds = if (parts[2].contains(".")) {
                    parts[2].split(".")[1].take(3).toLong()
                } else 0
                
                java.time.Duration.ofHours(hours)
                    .plusMinutes(minutes)
                    .plusSeconds(seconds)
                    .plusMillis(milliseconds)
            } else {
                null
            }
        } catch (e: Exception) {
            logger.warn(e) { "Failed to parse duration: $durationString" }
            null
        }
    }
}
