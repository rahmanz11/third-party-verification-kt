package com.external.verification.routes

import com.external.verification.models.*
import com.external.verification.services.FingerprintDeviceService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.serialization.json.Json
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
private val jsonPrinter = Json { 
    prettyPrint = true 
    isLenient = true 
    ignoreUnknownKeys = true 
}

fun Route.fingerprintRoutes(fingerprintDeviceService: FingerprintDeviceService) {
    
    route("/fingerprint") {
        
        // Device management endpoints
        post("/device/connect") {
            try {
                val request = call.receive<DeviceConnectionRequest>()
                logger.info { "Device connection request:\n${jsonPrinter.encodeToString(request)}" }
                
                val response = fingerprintDeviceService.connectDevice(request)
                call.respond(HttpStatusCode.OK, response)
                
            } catch (e: Exception) {
                logger.error(e) { "Error in device connection" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Device connection failed: ${e.message}"
                ))
            }
        }
        
        post("/device/disconnect") {
            try {
                val success = fingerprintDeviceService.disconnectDevice()
                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf(
                        "success" to true,
                        "message" to "Device disconnected successfully"
                    ))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf(
                        "error" to "Failed to disconnect device"
                    ))
                }
                
            } catch (e: Exception) {
                logger.error(e) { "Error in device disconnection" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Device disconnection failed: ${e.message}"
                ))
            }
        }
        
        get("/device/status") {
            try {
                val status = fingerprintDeviceService.getDeviceStatus()
                call.respond(HttpStatusCode.OK, status)
                
            } catch (e: Exception) {
                logger.error(e) { "Error getting device status" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Failed to get device status: ${e.message}"
                ))
            }
        }
        
        // Refresh device status to ensure it stays connected
        post("/device/refresh-status") {
            try {
                fingerprintDeviceService.refreshDeviceStatus()
                call.respond(HttpStatusCode.OK, DeviceStatusResponse(
                    success = true,
                    message = "Device status refreshed"
                ))
                
            } catch (e: Exception) {
                logger.error(e) { "Error refreshing device status" }
                call.respond(HttpStatusCode.InternalServerError, DeviceStatusResponse(
                    success = false,
                    message = "Failed to refresh device status: ${e.message}"
                ))
            }
        }
        
        // Lock device status to prevent disconnection during critical operations
        post("/device/lock-status") {
            try {
                fingerprintDeviceService.lockDeviceStatus()
                call.respond(HttpStatusCode.OK, DeviceStatusResponse(
                    success = true,
                    message = "Device status locked"
                ))
                
            } catch (e: Exception) {
                logger.error(e) { "Error locking device status" }
                call.respond(HttpStatusCode.InternalServerError, DeviceStatusResponse(
                    success = false,
                    message = "Failed to lock device status: ${e.message}"
                ))
            }
        }
        
        // Check if device is locked
        get("/device/lock-status") {
            try {
                val isLocked = fingerprintDeviceService.isDeviceLocked()
                call.respond(HttpStatusCode.OK, DeviceLockStatusResponse(
                    locked = isLocked
                ))
                
            } catch (e: Exception) {
                logger.error(e) { "Error checking device lock status" }
                call.respond(HttpStatusCode.InternalServerError, DeviceStatusResponse(
                    success = false,
                    message = "Failed to check device lock status: ${e.message}"
                ))
            }
        }
        
        // Check Digital Persona SDK initialization status
        get("/device/sdk-status") {
            try {
                val sdkStatus = fingerprintDeviceService.getSDKStatus()
                call.respond(HttpStatusCode.OK, sdkStatus)
                
            } catch (e: Exception) {
                logger.error(e) { "Error getting SDK status" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Failed to get SDK status: ${e.message}"
                ))
            }
        }
        
        // Manually reinitialize Digital Persona SDK
        post("/device/reinitialize-sdk") {
            try {
                val response = fingerprintDeviceService.reinitializeSDK()
                call.respond(HttpStatusCode.OK, response)
                
            } catch (e: Exception) {
                logger.error(e) { "Error reinitializing SDK" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Failed to reinitialize SDK: ${e.message}"
                ))
            }
        }
        
        // Fingerprint capture endpoints
        post("/capture") {
            try {
                val request = call.receive<FingerprintCaptureRequest>()
                logger.info { "Fingerprint capture request:\n${jsonPrinter.encodeToString(request)}" }
                
                val response = fingerprintDeviceService.captureFingerprint(request)
                call.respond(HttpStatusCode.OK, response)
                
            } catch (e: Exception) {
                logger.error(e) { "Error in fingerprint capture" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Fingerprint capture failed: ${e.message}"
                ))
            }
        }
        
        post("/capture/batch") {
            try {
                val request = call.receive<BatchFingerprintCaptureRequest>()
                logger.info { "Batch fingerprint capture request:\n${jsonPrinter.encodeToString(request)}" }
                
                val response = fingerprintDeviceService.captureBatchFingerprints(request)
                logger.info { "Batch fingerprint capture response: success=${response.success}, capturedFingers=${response.capturedFingers.size}, failedFingers=${response.failedFingers.size}" }
                logger.debug { "Fingerprint capture completed successfully" }
                
                call.respond(HttpStatusCode.OK, response)
                
            } catch (e: Exception) {
                logger.error(e) { "Error in batch fingerprint capture" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Batch fingerprint capture failed: ${e.message}"
                ))
            }
        }
        
        post("/capture/{fingerType}/cancel") {
            try {
                val fingerType = call.parameters["fingerType"]
                if (fingerType.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "fingerType parameter is required"
                    ))
                    return@post
                }
                
                val cancelled = fingerprintDeviceService.cancelCapture(fingerType)
                call.respond(HttpStatusCode.OK, mapOf(
                    "success" to cancelled,
                    "message" to if (cancelled) "Capture cancelled successfully" else "No active capture found"
                ))
                
            } catch (e: Exception) {
                logger.error(e) { "Error cancelling fingerprint capture" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Failed to cancel capture: ${e.message}"
                ))
            }
        }
        
        get("/capture/active") {
            try {
                val activeCaptures = fingerprintDeviceService.getActiveCaptures()
                call.respond(HttpStatusCode.OK, mapOf(
                    "activeCaptures" to activeCaptures,
                    "count" to activeCaptures.size
                ))
                
            } catch (e: Exception) {
                logger.error(e) { "Error getting active captures" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Failed to get active captures: ${e.message}"
                ))
            }
        }
        
        // Quality assessment endpoint
        post("/quality/assess") {
            try {
                val request = call.receive<Map<String, String>>()
                val imageData = request["imageData"]
                
                if (imageData.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "imageData is required"
                    ))
                    return@post
                }
                
                val quality = fingerprintDeviceService.assessFingerprintQuality(imageData)
                call.respond(HttpStatusCode.OK, quality)
                
            } catch (e: Exception) {
                logger.error(e) { "Error in quality assessment" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Quality assessment failed: ${e.message}"
                ))
            }
        }
        
        // WSQ conversion endpoint
        post("/convert/wsq") {
            try {
                val request = call.receive<Map<String, Any>>()
                val imageData = request["imageData"] as? String
                val quality = (request["quality"] as? Number)?.toInt() ?: 80
                
                if (imageData.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "imageData is required"
                    ))
                    return@post
                }
                
                // Note: convertToWSQ expects DPFPSample, not String
                // This endpoint needs to be updated to handle actual fingerprint samples
                call.respond(HttpStatusCode.BadRequest, mapOf(
                    "error" to "WSQ conversion requires DPFPSample, not image data string"
                ))
                
            } catch (e: Exception) {
                logger.error(e) { "Error in WSQ conversion" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "WSQ conversion failed: ${e.message}"
                ))
            }
        }
    }
    
    // WebSocket endpoint for real-time fingerprint device communication
    webSocket("/fingerprint/ws") {
        try {
            logger.info { "WebSocket connection established for fingerprint device" }
            
            // Send initial connection message
            send(FingerprintWebSocketMessage.DeviceConnected(
                DeviceInfo(
                    deviceId = "WS_DEVICE_001",
                    deviceName = "WebSocket Fingerprint Device",
                    deviceType = "websocket_fingerprint",
                    capabilities = listOf("real_time_capture", "live_quality_assessment")
                )
            ).let { jsonPrinter.encodeToString(FingerprintWebSocketMessage.serializer(), it) })
            
            // Handle incoming messages
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        logger.info { "Received WebSocket message: $text" }
                        
                        try {
                            // Parse the message and handle accordingly
                            // This is a simplified implementation - expand based on your needs
                            when {
                                text.contains("capture") -> {
                                    // Handle capture request
                                    send(FingerprintWebSocketMessage.CaptureStarted("RIGHT_THUMB")
                                        .let { jsonPrinter.encodeToString(FingerprintWebSocketMessage.serializer(), it) })
                                }
                                text.contains("status") -> {
                                    // Send device status
                                    val status = fingerprintDeviceService.getDeviceStatus()
                                    send(jsonPrinter.encodeToString(FingerprintDeviceStatus.serializer(), status))
                                }
                                else -> {
                                    // Echo back for testing
                                    send(Frame.Text("Echo: $text"))
                                }
                            }
                        } catch (e: Exception) {
                            logger.error(e) { "Error processing WebSocket message" }
                            send(Frame.Text("Error: ${e.message}"))
                        }
                    }
                    is Frame.Close -> {
                        logger.info { "WebSocket connection closed" }
                        break
                    }
                    else -> {
                        // Handle other frame types if needed
                    }
                }
            }
            
        } catch (e: ClosedSendChannelException) {
            logger.info { "WebSocket connection closed by client" }
        } catch (e: Exception) {
            logger.error(e) { "Error in WebSocket connection" }
        } finally {
            logger.info { "WebSocket connection terminated" }
        }
    }
}
