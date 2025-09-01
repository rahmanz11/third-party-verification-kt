package com.external.verification.routes

import com.external.verification.models.*
import com.external.verification.services.ThirdPartyApiService
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

fun Route.afisRoutes(thirdPartyApiService: ThirdPartyApiService) {
    
    route("/afis") {
        
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
    }
}
