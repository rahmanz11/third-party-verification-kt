package com.external.verification.routes

import com.external.verification.models.*
import com.external.verification.services.ThirdPartyApiService
import com.external.verification.services.FingerprintDeviceService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}
private val jsonPrinter = Json { 
    prettyPrint = true 
    isLenient = true 
    ignoreUnknownKeys = true 
}

fun Route.afisRoutes(thirdPartyApiService: ThirdPartyApiService, fingerprintDeviceService: FingerprintDeviceService) {
    
    route("/afis") {
        
        // Enhanced AFIS verification with fingerprint capture
        post("/verification-with-capture") {
            try {
                val request = call.receive<AfisVerificationWithCaptureRequest>()
                logger.info { "AFIS Verification with Capture Request: ${jsonPrinter.encodeToString(request)}" }
                
                // Get JWT from Authorization header
                val authHeader = call.request.header("Authorization")
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf(
                        "error" to "Authorization header with Bearer token is required"
                    ))
                    return@post
                }
                
                val jwt = authHeader.substringAfter("Bearer ")
                
                // Validate request
                if (request.fingerEnums.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "At least one finger must be specified in fingerEnums"
                    ))
                    return@post
                }
                
                if (request.nid10Digit.isNullOrBlank() && request.nid17Digit.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "Either nid10Digit or nid17Digit must be provided"
                    ))
                    return@post
                }
                
                if (request.dateOfBirth.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "dateOfBirth is required"
                    ))
                    return@post
                }
                
                // Step 1: Capture fingerprints for specified fingers
                val capturedFingerprints = mutableListOf<FingerprintCaptureResponse>()
                val failedFingers = mutableListOf<String>()
                
                for (fingerType in request.fingerEnums) {
                    try {
                        val captureRequest = FingerprintCaptureRequest(
                            fingerType = fingerType,
                            qualityThreshold = request.qualityThreshold ?: 70,
                            captureTimeout = request.captureTimeout ?: 30000,
                            retryCount = request.retryCount ?: 3
                        )
                        
                        val captureResponse = fingerprintDeviceService.captureFingerprint(captureRequest)
                        if (captureResponse.success) {
                            capturedFingerprints.add(captureResponse)
                            logger.info { "Successfully captured ${fingerType}" }
                        } else {
                            failedFingers.add(fingerType)
                            logger.warn { "Failed to capture ${fingerType}: ${captureResponse.error}" }
                        }
                    } catch (e: Exception) {
                        failedFingers.add(fingerType)
                        logger.error(e) { "Exception during capture of ${fingerType}" }
                    }
                }
                
                // Step 2: Check if we have enough fingerprints for AFIS verification
                if (capturedFingerprints.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "Failed to capture any fingerprints",
                        "failedFingers" to failedFingers
                    ))
                    return@post
                }
                
                // Step 3: Create AFIS verification request with captured fingerprints
                val afisRequest = AfisVerificationRequest(
                    dateOfBirth = request.dateOfBirth,
                    nid10Digit = request.nid10Digit,
                    nid17Digit = request.nid17Digit,
                    fingerEnums = capturedFingerprints.map { it.fingerType }
                )
                
                // Step 4: Submit to AFIS verification
                val afisResponse = thirdPartyApiService.afisVerification(afisRequest, jwt)
                
                // Step 5: Return comprehensive response
                val response = AfisVerificationWithCaptureResponse(
                    afisVerification = afisResponse,
                    fingerprintCapture = FingerprintCaptureSummary(
                        totalRequested = request.fingerEnums.size,
                        successfullyCaptured = capturedFingerprints.size,
                        failedFingers = failedFingers,
                        capturedFingerprints = capturedFingerprints.map { 
                            CapturedFingerInfo(
                                fingerType = it.fingerType,
                                qualityScore = it.qualityScore,
                                hasImageData = !it.imageData.isNullOrBlank(),
                                hasWsqData = !it.wsqData.isNullOrBlank(),
                                captureTime = it.captureTime
                            )
                        }
                    )
                )
                
                call.respond(HttpStatusCode.OK, response)
                
            } catch (e: Exception) {
                logger.error(e) { "Error in AFIS verification with capture" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Internal server error: ${e.message}"
                ))
            }
        }
        
        // Batch fingerprint capture for AFIS verification
        post("/capture-fingerprints") {
            try {
                val request = call.receive<AfisFingerprintCaptureRequest>()
                logger.info { "AFIS Fingerprint Capture Request: ${jsonPrinter.encodeToString(request)}" }
                
                // Validate request
                if (request.fingerEnums.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "At least one finger must be specified in fingerEnums"
                    ))
                    return@post
                }
                
                // Capture fingerprints
                val batchRequest = BatchFingerprintCaptureRequest(
                    fingers = request.fingerEnums,
                    qualityThreshold = request.qualityThreshold ?: 70,
                    captureTimeout = request.captureTimeout ?: 60000,
                    retryCount = request.retryCount ?: 2
                )
                
                val batchResponse = fingerprintDeviceService.captureBatchFingerprints(batchRequest)
                
                if (batchResponse.success) {
                    val response = AfisFingerprintCaptureResponse(
                        success = true,
                        capturedFingerprints = batchResponse.capturedFingers.map { 
                            CapturedFingerInfo(
                                fingerType = it.fingerType,
                                qualityScore = it.qualityScore,
                                hasImageData = !it.imageData.isNullOrBlank(),
                                hasWsqData = !it.wsqData.isNullOrBlank(),
                                captureTime = it.captureTime
                            )
                        },
                        failedFingers = batchResponse.failedFingers.map { it.fingerType },
                        totalTime = batchResponse.totalTime,
                        qualityThreshold = request.qualityThreshold ?: 70
                    )
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf(
                        "error" to "Fingerprint capture failed: ${batchResponse.error}"
                    ))
                }
                
            } catch (e: Exception) {
                logger.error(e) { "Error in AFIS fingerprint capture" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Internal server error: ${e.message}"
                ))
            }
        }
        
        // Original AFIS verification endpoint (unchanged)
        post("/verification-secured") {
            try {
                val afisRequest = call.receive<AfisVerificationRequest>()
                logger.info { "AFIS Verification Request: ${jsonPrinter.encodeToString(afisRequest)}" }
                
                // Get JWT from Authorization header
                val authHeader = call.request.header("Authorization")
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf(
                        "error" to "Authorization header with Bearer token is required"
                    ))
                    return@post
                }
                
                val jwt = authHeader.substringAfter("Bearer ")
                
                // Validate request
                if (afisRequest.fingerEnums.isEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "At least one finger must be specified in fingerEnums"
                    ))
                    return@post
                }
                
                if (afisRequest.nid10Digit.isNullOrBlank() && afisRequest.nid17Digit.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "Either nid10Digit or nid17Digit must be provided"
                    ))
                    return@post
                }
                
                if (afisRequest.dateOfBirth.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "dateOfBirth is required"
                    ))
                    return@post
                }
                
                val response = thirdPartyApiService.afisVerification(afisRequest, jwt)
                call.respond(HttpStatusCode.OK, response)
                
            } catch (e: Exception) {
                logger.error(e) { "Error in AFIS verification" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Internal server error: ${e.message}"
                ))
            }
        }
        
        get("/verification/result/{jobId}") {
            try {
                val jobId = call.parameters["jobId"]
                if (jobId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf(
                        "error" to "jobId parameter is required"
                    ))
                    return@get
                }
                
                val authHeader = call.request.header("Authorization")
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf(
                        "error" to "Authorization header with Bearer token is required"
                    ))
                    return@get
                }
                
                val jwt = authHeader.substringAfter("Bearer ")
                
                val resultCheckUrl = "https://prportal.nidw.gov.bd/partner-service/rest/afis/verification/result/$jobId"
                
                val response = thirdPartyApiService.checkAfisResult(resultCheckUrl, jwt)
                call.respond(HttpStatusCode.OK, response)
                
            } catch (e: Exception) {
                logger.error(e) { "Error in AFIS result check" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Internal server error: ${e.message}"
                ))
            }
        }
        
        // Device status for AFIS operations
        get("/device/status") {
            try {
                val deviceStatus = fingerprintDeviceService.getDeviceStatus()
                val sdkStatus = fingerprintDeviceService.getSDKStatus()
                
                val response = mapOf(
                    "deviceStatus" to deviceStatus,
                    "sdkStatus" to sdkStatus,
                    "afisReady" to deviceStatus.connected
                )
                
                call.respond(HttpStatusCode.OK, response)
                
            } catch (e: Exception) {
                logger.error(e) { "Error getting device status for AFIS" }
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Failed to get device status: ${e.message}"
                ))
            }
        }
    }
}
