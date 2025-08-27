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
                    
                    // Validate request
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
                    
                    // Check if at least one NID is provided
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
                    val errorResponse = VerificationResponse(
                        status = "ERROR",
                        statusCode = "ERROR",
                        message = e.message ?: "Verification failed"
                    )
                    call.respond(HttpStatusCode.InternalServerError, errorResponse)
                }
            }
        }
    }
}
