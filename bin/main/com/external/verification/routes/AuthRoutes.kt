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


fun Route.authRoutes(thirdPartyApiService: ThirdPartyApiService) {
    
    route("/auth") {
        
        post("/login") {
            try {
                val loginRequest = call.receive<LoginRequest>()
                
                // Validate request
                if (loginRequest.username.isBlank() || loginRequest.password.isBlank()) {
                    val errorResponse = LoginResponse(
                        status = "BAD_REQUEST",
                        statusCode = "ERROR",
                        error = LoginResponse.ErrorData("validation", "Username and password are required")
                    )
                    call.respond(HttpStatusCode.BadRequest, errorResponse)
                    return@post
                }
                
                val response = thirdPartyApiService.login(loginRequest)
                call.respond(HttpStatusCode.OK, response)
                
            } catch (e: Exception) {
                val errorResponse = LoginResponse(
                    status = "ERROR",
                    statusCode = "ERROR",
                    error = LoginResponse.ErrorData("login", e.message ?: "Login failed")
                )
                call.respond(HttpStatusCode.Unauthorized, errorResponse)
            }
        }
        
        authenticate("auth-jwt") {
            post("/logout") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val accessToken = principal?.get("token") ?: throw Exception("No token found")
                    
                    thirdPartyApiService.logout(accessToken)
                    
                    val response = ApiResponse(
                        status = "OK",
                        statusCode = "SUCCESS",
                        success = ApiResponse.SuccessData("Logout Successful")
                    )
                    call.respond(HttpStatusCode.OK, response)
                    
                } catch (e: Exception) {
                    val errorResponse = ApiResponse(
                        status = "ERROR",
                        statusCode = "ERROR",
                        error = ApiResponse.ErrorData("logout", e.message ?: "Logout failed")
                    )
                    call.respond(HttpStatusCode.InternalServerError, errorResponse)
                }
            }
            
            post("/change-user-password") {
                try {
                    val changePasswordRequest = call.receive<ChangePasswordRequest>()
                    
                    // Validate request
                    if (changePasswordRequest.currentPassword.isBlank() || 
                        changePasswordRequest.newPassword.isBlank() || 
                        changePasswordRequest.confirmPassword.isBlank()) {
                        val errorResponse = ApiResponse(
                            status = "BAD_REQUEST",
                            statusCode = "ERROR",
                            error = ApiResponse.ErrorData("validation", "All password fields are required")
                        )
                        call.respond(HttpStatusCode.BadRequest, errorResponse)
                        return@post
                    }
                    
                    if (changePasswordRequest.newPassword.length < 6 || changePasswordRequest.newPassword.length > 50) {
                        val errorResponse = ApiResponse(
                            status = "BAD_REQUEST",
                            statusCode = "ERROR",
                            error = ApiResponse.ErrorData("validation", "New password must be between 6 and 50 characters")
                        )
                        call.respond(HttpStatusCode.BadRequest, errorResponse)
                        return@post
                    }
                    
                    if (changePasswordRequest.newPassword != changePasswordRequest.confirmPassword) {
                        val errorResponse = ApiResponse(
                            status = "BAD_REQUEST",
                            statusCode = "ERROR",
                            error = ApiResponse.ErrorData("confirmPassword", "Passwords do not match")
                        )
                        call.respond(HttpStatusCode.BadRequest, errorResponse)
                        return@post
                    }
                    
                    val principal = call.principal<JWTPrincipal>()
                    val accessToken = principal?.get("token") ?: throw Exception("No token found")
                    
                    thirdPartyApiService.changePassword(changePasswordRequest, accessToken)
                    
                    val response = ApiResponse(
                        status = "OK",
                        statusCode = "SUCCESS",
                        success = ApiResponse.SuccessData("Password Updated Successfully")
                    )
                    call.respond(HttpStatusCode.OK, response)
                    
                } catch (e: Exception) {
                    val errorResponse = ApiResponse(
                        status = "BAD_REQUEST",
                        statusCode = "ERROR",
                        error = ApiResponse.ErrorData("password", e.message ?: "Password change failed")
                    )
                    call.respond(HttpStatusCode.BadRequest, errorResponse)
                }
            }
        }
    }
}
