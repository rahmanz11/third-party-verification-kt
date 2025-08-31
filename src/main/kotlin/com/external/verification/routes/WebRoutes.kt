package com.external.verification.routes

import com.external.verification.services.JwtStorageService
import com.external.verification.services.ThirdPartyApiService
import com.external.verification.services.BasicAuthSessionService
import com.external.verification.models.ThirdPartyLogoutResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.freemarker.*


fun Route.webRoutes(
    thirdPartyApiService: ThirdPartyApiService,
    jwtStorageService: JwtStorageService,
    basicAuthSessionService: BasicAuthSessionService
) {
    
    get("/") {
        call.respond(FreeMarkerContent("login.ftl", mapOf(
            "title" to "Login - Verification System"
        )))
    }
    
    post("/login") {
        try {
            val formData = call.receiveParameters()
            val username = formData["username"] ?: ""
            val password = formData["password"] ?: ""
            
            if (username.isBlank() || password.isBlank()) {
                call.respond(FreeMarkerContent("login.ftl", mapOf(
                    "title" to "Login - Verification System",
                    "error" to "Please provide valid credentials"
                )))
                return@post
            }
            if (username == "admin" && password == "admin123") {
                kotlinx.coroutines.runBlocking { basicAuthSessionService.createSession(username) }
                call.respondRedirect("/dashboard?username=$username")
            } else {
                call.respond(FreeMarkerContent("login.ftl", mapOf(
                    "title" to "Login - Verification System",
                    "error" to "Invalid username or password"
                )))
            }
            
        } catch (e: Exception) {
            call.respond(FreeMarkerContent("login.ftl", mapOf(
                "title" to "Login - Verification System",
                "error" to "Login failed: ${e.message}"
            )))
        }
    }
    
    get("/dashboard") {
        val username = call.request.queryParameters["username"] ?: ""
        
        if (username.isBlank()) {
            call.respondRedirect("/")
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        
        if (!isValidSession) {
            call.respondRedirect("/")
            return@get
        }
        
        call.respond(FreeMarkerContent("dashboard.ftl", mapOf(
            "title" to "Dashboard - Verification System",
            "username" to username
        )))
    }

    get("/third-party-login") {
        val username = call.request.queryParameters["username"] ?: ""
        
        if (username.isBlank()) {
            call.respondRedirect("/")
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respondRedirect("/")
            return@get
        }
        
        call.respond(FreeMarkerContent("third-party-login.ftl", mapOf(
            "title" to "Third Party Login - Verification System",
            "username" to username
        )))
    }

    post("/third-party-login") {
        val formData = call.receiveParameters()
        val username = formData["username"] ?: ""
        val thirdPartyUsername = formData["thirdPartyUsername"] ?: ""
        val thirdPartyPassword = formData["thirdPartyPassword"] ?: ""
        
        try {
            if (thirdPartyUsername.isBlank() || thirdPartyPassword.isBlank()) {
                val errorMessage = "Please provide valid credentials"
                
                if (call.request.header("X-Requested-With") == "XMLHttpRequest") {
                    call.respond(HttpStatusCode.OK, com.external.verification.models.ThirdPartyLoginResponse(
                        success = false,
                        error = errorMessage
                    ))
                } else {
                    call.respond(FreeMarkerContent("third-party-login.ftl", mapOf(
                        "title" to "Third Party Login - Verification System",
                        "username" to username,
                        "error" to errorMessage
                    )))
                }
                return@post
            }

            val loginRequest = com.external.verification.models.LoginRequest(thirdPartyUsername, thirdPartyPassword)
            val loginResponse = thirdPartyApiService.login(loginRequest)
            
            if (loginResponse.status == "OK" && loginResponse.success != null) {
                val accessToken = loginResponse.success.data.access_token
                val refreshToken = loginResponse.success.data.refresh_token
                
                jwtStorageService.storeJwt(thirdPartyUsername, accessToken, refreshToken)
                
                if (call.request.header("X-Requested-With") == "XMLHttpRequest") {
                    call.respond(HttpStatusCode.OK, com.external.verification.models.ThirdPartyLoginResponse(
                        success = true,
                        message = "Third-party authentication successful!"
                    ))
                } else {
                    call.respondRedirect("/verification-form?username=$username&thirdPartyUsername=$thirdPartyUsername")
                }
            } else {
                val errorMessage = loginResponse.error?.message ?: "Third-party login failed"
                
                if (call.request.header("X-Requested-With") == "XMLHttpRequest") {
                    call.respond(HttpStatusCode.OK, com.external.verification.models.ThirdPartyLoginResponse(
                        success = false,
                        error = errorMessage
                    ))
                } else {
                    call.respond(FreeMarkerContent("third-party-login.ftl", mapOf(
                        "title" to "Third Party Login - Verification System",
                        "username" to username,
                        "error" to errorMessage
                    )))
                }
            }
            
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("503") == true || e.message?.contains("Service Unavailable") == true -> 
                    "Third party service is currently unavailable. Please try again later."
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    "Third party service not available"
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    "Invalid third-party credentials. Please check your username and password."
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    "Invalid request data. Please check your credentials and try again."
                e.message?.contains("NoTransformationFoundException") == true -> 
                    "Third party service is not responding properly. Please try again later."
                e.message?.contains("ContentType") == true || e.message?.contains("text/html") == true -> 
                    "Third party service returned an unexpected response. Please try again later."
                e.message?.contains("Connection") == true || e.message?.contains("timeout") == true -> 
                    "Unable to connect to third party service. Please check your connection and try again."
                else -> "Third-party login service temporarily unavailable. Please try again later."
            }
            
            if (call.request.header("X-Requested-With") == "XMLHttpRequest") {
                call.respond(HttpStatusCode.OK, com.external.verification.models.ThirdPartyLoginResponse(
                    success = false,
                    error = errorMessage
                ))
            } else {
                call.respond(FreeMarkerContent("third-party-login.ftl", mapOf(
                    "title" to "Third Party Login - Verification System",
                    "username" to username,
                    "error" to errorMessage
                )))
            }
        }
    }

    get("/afis-verification") {
        val username = call.request.queryParameters["username"] ?: ""
        val thirdPartyUsername = call.request.queryParameters["thirdPartyUsername"] ?: username
        
        if (username.isBlank() || thirdPartyUsername.isBlank()) {
            call.respondRedirect("/")
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        
        if (!isValidSession) {
            call.respondRedirect("/")
            return@get
        }
        
        call.respond(FreeMarkerContent("afis-verification.ftl", mapOf(
            "title" to "AFIS Verification - Verification System",
            "username" to username,
            "thirdPartyUsername" to thirdPartyUsername
        )))
    }

    get("/fingerprint-capture") {
        val username = call.request.queryParameters["username"] ?: ""
        val thirdPartyUsername = call.request.queryParameters["thirdPartyUsername"] ?: username
        
        if (username.isBlank() || thirdPartyUsername.isBlank()) {
            call.respondRedirect("/")
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        
        if (!isValidSession) {
            call.respondRedirect("/")
            return@get
        }
        
        call.respond(FreeMarkerContent("fingerprint-capture.ftl", mapOf(
            "title" to "Fingerprint Capture - Verification System",
            "username" to username,
            "thirdPartyUsername" to thirdPartyUsername
        )))
    }

    get("/verification-form") {
        val username = call.request.queryParameters["username"] ?: ""
        val thirdPartyUsername = call.request.queryParameters["thirdPartyUsername"] ?: username
        
        if (username.isBlank() || thirdPartyUsername.isBlank()) {
            call.respondRedirect("/")
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        
        if (!isValidSession) {
            call.respondRedirect("/")
            return@get
        }
        
        call.respond(FreeMarkerContent("verification-form.ftl", mapOf(
            "title" to "Person Verification - Verification System",
            "username" to username,
            "thirdPartyUsername" to thirdPartyUsername
        )))
    }
    
    post("/verify") {
        val formData = call.receiveParameters()
        val username = formData["username"] ?: ""
        val thirdPartyUsername = formData["thirdPartyUsername"] ?: ""
        
        if (username.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username is required"))
            return@post
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Authentication required"))
            return@post
        }
        
        try {
            val jwt = jwtStorageService.getValidJwt(thirdPartyUsername)
            
            if (jwt == null) {
                call.respondRedirect("/third-party-login?username=$username")
                return@post
            }
            
            val verificationRequest = com.external.verification.models.VerificationRequest(
                identify = com.external.verification.models.VerificationRequest.Identify(
                    nid10Digit = if (formData["nidType"] == "10digit") formData["nidValue"]?.takeIf { it.isNotBlank() } else null,
                    nid17Digit = if (formData["nidType"] == "17digit") formData["nidValue"]?.takeIf { it.isNotBlank() } else null
                ),
                verify = com.external.verification.models.VerificationRequest.Verify(
                    nameEn = formData["nameEn"] ?: "",
                    name = formData["name"] ?: "",
                    dateOfBirth = formData["dateOfBirth"] ?: "",
                    father = formData["father"] ?: "",
                    mother = formData["mother"] ?: "",
                    spouse = formData["spouse"] ?: "",
                    permanentAddress = com.external.verification.models.VerificationRequest.Address(
                        division = formData["permanentDivision"] ?: "",
                        district = formData["permanentDistrict"] ?: "",
                        upozila = formData["permanentUpazila"] ?: ""
                    ),
                    presentAddress = com.external.verification.models.VerificationRequest.Address(
                        division = formData["presentDivision"] ?: "",
                        district = formData["presentDistrict"] ?: "",
                        upozila = formData["presentUpazila"] ?: ""
                    )
                )
            )
            
            val verificationResponse = thirdPartyApiService.verifyPerson(verificationRequest, jwt)
            
            call.respond(FreeMarkerContent("verification-result.ftl", mapOf(
                "title" to "Verification Result - Verification System",
                "username" to username,
                "thirdPartyUsername" to thirdPartyUsername,
                "verificationResponse" to verificationResponse
            )))
            
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    "Third party service not available"
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    "Authentication failed. Please login to third-party service again."
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    "Invalid verification data. Please check your information and try again."
                else -> "Verification service temporarily unavailable. Please try again later."
            }
            
            call.respond(FreeMarkerContent("verification-form.ftl", mapOf(
                "title" to "Person Verification - Verification System",
                "username" to username,
                "thirdPartyUsername" to thirdPartyUsername,
                "error" to errorMessage
            )))
        }
    }
    
    get("/api/jwt-status") {
        val username = call.request.queryParameters["username"] ?: ""
        val thirdPartyUsername = call.request.queryParameters["thirdPartyUsername"] ?: username
        
        if (username.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username is required"))
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Authentication required"))
            return@get
        }
        
        val isJwtValid = jwtStorageService.isJwtValid(thirdPartyUsername)
        val storedUsernames = jwtStorageService.getStoredUsernames()
        
        // Get expiration time if JWT exists
        val expiresAt = if (isJwtValid) {
            val storedJwt = jwtStorageService.getStoredJwt(thirdPartyUsername)
            storedJwt?.expiresAt?.toString()
        } else null
        
        val response = com.external.verification.models.JwtStatusResponse(
            isValid = isJwtValid,
            username = username,
            thirdPartyUsername = thirdPartyUsername,
            hasStoredJwt = storedUsernames.contains(thirdPartyUsername),
            storedUsernames = storedUsernames,
            expiresAt = expiresAt
        )
        
        call.respond(HttpStatusCode.OK, response)
    }
    
    get("/change-password") {
        val username = call.request.queryParameters["username"] ?: ""
        val thirdPartyUsername = call.request.queryParameters["thirdPartyUsername"] ?: username
        
        if (username.isBlank()) {
            call.respondRedirect("/")
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respondRedirect("/")
            return@get
        }
        
        call.respond(FreeMarkerContent("change-password.ftl", mapOf(
            "title" to "Change Password - Verification System",
            "username" to username,
            "thirdPartyUsername" to thirdPartyUsername
        )))
    }
    
    post("/change-password") {
        val formData = call.receiveParameters()
        val username = formData["username"] ?: ""
        val thirdPartyUsername = formData["thirdPartyUsername"] ?: username
        
        if (username.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username is required"))
            return@post
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Authentication required"))
            return@post
        }
        
        try {
            val jwt = jwtStorageService.getValidJwt(thirdPartyUsername)
            
            if (jwt == null) {
                call.respond(FreeMarkerContent("change-password.ftl", mapOf(
                    "title" to "Change Password - Verification System",
                    "username" to username,
                    "thirdPartyUsername" to thirdPartyUsername,
                    "error" to "JWT token expired or not found. Please login to third-party service first."
                )))
                return@post
            }
            
            val changePasswordRequest = com.external.verification.models.ChangePasswordRequest(
                currentPassword = formData["currentPassword"] ?: "",
                newPassword = formData["newPassword"] ?: "",
                confirmPassword = formData["confirmPassword"] ?: ""
            )
            
            val response = thirdPartyApiService.changePassword(changePasswordRequest, jwt)
            
            jwtStorageService.removeJwt(thirdPartyUsername)
            
            call.respond(FreeMarkerContent("change-password.ftl", mapOf(
                "title" to "Change Password - Verification System",
                "username" to username,
                "thirdPartyUsername" to thirdPartyUsername,
                "success" to "Password changed successfully! Your session has been terminated. Please login again."
            )))
            
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    "Third party service not available"
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    "Authentication failed. Please login to third-party service again."
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    "Invalid password data. Please check your information and try again."
                else -> "Password change service temporarily unavailable. Please try again later."
            }
            
            call.respond(FreeMarkerContent("change-password.ftl", mapOf(
                "title" to "Change Password - Verification System",
                "username" to username,
                "thirdPartyUsername" to thirdPartyUsername,
                "error" to errorMessage
            )))
        }
    }
    
    get("/partner-billing") {
        val username = call.request.queryParameters["username"] ?: ""
        val thirdPartyUsername = call.request.queryParameters["thirdPartyUsername"] ?: username
        
        if (username.isBlank()) {
            call.respondRedirect("/")
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respondRedirect("/")
            return@get
        }
        
        call.respond(FreeMarkerContent("partner-billing.ftl", mapOf(
            "title" to "Partner Billing - Verification System",
            "username" to username,
            "thirdPartyUsername" to thirdPartyUsername
        )))
    }
    
    post("/partner-billing") {
        val formData = call.receiveParameters()
        val username = formData["username"] ?: ""
        val thirdPartyUsername = formData["thirdPartyUsername"] ?: username
        
        if (username.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username is required"))
            return@post
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Authentication required"))
            return@post
        }
        
        try {
            val jwt = jwtStorageService.getValidJwt(thirdPartyUsername)
            
            if (jwt == null) {
                call.respond(FreeMarkerContent("partner-billing.ftl", mapOf(
                    "title" to "Partner Billing - Verification System",
                    "username" to username,
                    "thirdPartyUsername" to thirdPartyUsername,
                    "error" to "JWT token expired or not found. Please login to third-party service first."
                )))
                return@post
            }
            
            val billingRequest = com.external.verification.models.BillingRequest(
                startDate = formData["startDate"] ?: "",
                endDate = formData["endDate"] ?: ""
            )
            
            val billingResponse = thirdPartyApiService.getBillingReport(billingRequest, jwt)
            
            call.respond(FreeMarkerContent("partner-billing.ftl", mapOf(
                "title" to "Partner Billing - Verification System",
                "username" to username,
                "thirdPartyUsername" to thirdPartyUsername,
                "billingResponse" to billingResponse
            )))
            
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    "Third party service not available"
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    "Authentication failed. Please login to third-party service again."
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    "Invalid billing data. Please check your date range and try again."
                else -> "Billing service temporarily unavailable. Please try again later."
            }
            
            call.respond(FreeMarkerContent("partner-billing.ftl", mapOf(
                "title" to "Partner Billing - Verification System",
                "username" to username,
                "thirdPartyUsername" to thirdPartyUsername,
                "error" to errorMessage
            )))
        }
    }
    
    get("/logout") {
        val username = call.request.queryParameters["username"] ?: ""
        val thirdPartyUsername = call.request.queryParameters["thirdPartyUsername"] ?: username
        
        if (username.isNotBlank()) {
            kotlinx.coroutines.runBlocking { basicAuthSessionService.removeSession(username) }
        }
        
        // Call third-party logout API if we have a JWT
        if (thirdPartyUsername.isNotBlank()) {
            try {
                val jwt = jwtStorageService.getValidJwt(thirdPartyUsername)
                if (jwt != null) {
                    kotlinx.coroutines.runBlocking { 
                        thirdPartyApiService.logout(jwt)
                        jwtStorageService.removeJwt(thirdPartyUsername)
                    }
                }
            } catch (e: Exception) {
                // Log the error but don't fail the logout process
                println("Error during third-party logout: ${e.message}")
            }
        }
        
        call.respondRedirect("/")
    }
    
    post("/third-party-logout") {
        val formData = call.receiveParameters()
        val username = formData["username"] ?: ""
        val thirdPartyUsername = formData["thirdPartyUsername"] ?: username
        
        if (username.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username is required"))
            return@post
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Authentication required"))
            return@post
        }
        
        try {
            val jwt = jwtStorageService.getValidJwt(thirdPartyUsername)
            
            if (jwt == null) {
                call.respond(HttpStatusCode.BadRequest, ThirdPartyLogoutResponse(
                    success = false,
                    message = "No JWT token found. You are not logged in to the third-party service."
                ))
                return@post
            }
            
            val logoutResponse = thirdPartyApiService.logout(jwt)
            
            if (logoutResponse.status == "OK" && logoutResponse.statusCode == "SUCCESS") {
                // Only remove JWT if third-party logout was successful
                jwtStorageService.removeJwt(thirdPartyUsername)
                
                call.respond(HttpStatusCode.OK, ThirdPartyLogoutResponse(
                    success = true,
                    message = "Successfully logged out from third-party service"
                ))
            } else {
                val errorMessage = logoutResponse.error?.message ?: "Unknown error"
                call.respond(HttpStatusCode.BadRequest, ThirdPartyLogoutResponse(
                    success = false,
                    message = "Third-party logout failed: $errorMessage"
                ))
            }
            
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    "Third party service not available"
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    "Authentication failed. JWT token may be expired."
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    "Invalid request to third-party service"
                else -> "Third-party logout service temporarily unavailable. Please try again later."
            }
            
            call.respond(HttpStatusCode.BadRequest, ThirdPartyLogoutResponse(
                success = false,
                message = errorMessage
            ))
        }
    }
}
