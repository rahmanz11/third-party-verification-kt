package com.external.verification.routes

import com.external.verification.models.*
import com.external.verification.services.ThirdPartyApiService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.verificationRoutes(thirdPartyApiService: ThirdPartyApiService) {
    
    route("/demographic") {
        
        authenticate("auth-jwt") {
            post("/verification") {
                try {
                    val verificationRequest = call.receive<VerificationRequest>()
                    
                    if (verificationRequest.verify.nameEn.isBlank() || 
                        verificationRequest.verify.name.isBlank() || 
                        verificationRequest.verify.dateOfBirth.isBlank() || 
                        verificationRequest.verify.father.isBlank() || 
                        verificationRequest.verify.mother.isBlank()) {
                        val errorResponse = VerificationResponse(
                            status = "BAD_REQUEST",
                            statusCode = "ERROR",
                            message = "All mandatory fields are required"
                        )
                        call.respond(HttpStatusCode.BadRequest, errorResponse)
                        return@post
                    }
                    
                    if (verificationRequest.identify.nid10Digit.isNullOrBlank() && 
                        verificationRequest.identify.nid17Digit.isNullOrBlank()) {
                        val errorResponse = VerificationResponse(
                            status = "BAD_REQUEST",
                            statusCode = "ERROR",
                            message = "Search permission without one of the mandatory fields is not allowed"
                        )
                        call.respond(HttpStatusCode.BadRequest, errorResponse)
                        return@post
                    }
                    
                    val principal = call.principal<JWTPrincipal>()
                    val accessToken = principal?.get("token") ?: throw Exception("No token found")
                    
                    val response = thirdPartyApiService.verifyPerson(verificationRequest, accessToken)
                    call.respond(HttpStatusCode.OK, response)
                    
                } catch (e: Exception) {
                    val errorMessage = when {
                        e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                            "Third party service not available"
                        e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                            "Authentication failed. Please login again."
                        e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                            "Invalid verification data. Please check your information."
                        else -> "Verification service temporarily unavailable. Please try again later."
                    }
                    
                    val errorResponse = VerificationResponse(
                        status = "ERROR",
                        statusCode = "ERROR",
                        message = errorMessage
                    )
                    call.respond(HttpStatusCode.InternalServerError, errorResponse)
                }
            }
        }
    }
}
