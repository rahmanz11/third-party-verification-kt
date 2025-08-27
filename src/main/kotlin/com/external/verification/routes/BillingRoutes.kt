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


fun Route.billingRoutes(thirdPartyApiService: ThirdPartyApiService) {
    
    route("/partner-billing") {
        
        authenticate("auth-jwt") {
            post("/get-billing-report") {
                try {
                    val billingRequest = call.receive<BillingRequest>()
                    
                    val dateRegex = Regex("\\d{4}-\\d{2}-\\d{2}")
                    if (!dateRegex.matches(billingRequest.startDate) || !dateRegex.matches(billingRequest.endDate)) {
                        val errorResponse = ApiResponse(
                            status = "BAD_REQUEST",
                            statusCode = "ERROR",
                            error = ApiResponse.ErrorData("validation", "Date format must be YYYY-MM-DD")
                        )
                        call.respond(HttpStatusCode.BadRequest, errorResponse)
                        return@post
                    }
                    
                    val principal = call.principal<JWTPrincipal>()
                    val accessToken = principal?.get("token") ?: throw Exception("No token found")
                    
                    val response = thirdPartyApiService.getBillingReport(billingRequest, accessToken)
                    call.respond(HttpStatusCode.OK, response)
                    
                } catch (e: Exception) {
                    val errorMessage = when {
                        e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                            "Third party service not available"
                        e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                            "Authentication failed. Please login again."
                        e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                            "Invalid billing data. Please check your date range."
                        else -> "Billing service temporarily unavailable. Please try again later."
                    }
                    
                    val errorResponse = ApiResponse(
                        status = "ERROR",
                        statusCode = "ERROR",
                        error = ApiResponse.ErrorData("billing", errorMessage)
                    )
                    call.respond(HttpStatusCode.InternalServerError, errorResponse)
                }
            }
        }
    }
}
