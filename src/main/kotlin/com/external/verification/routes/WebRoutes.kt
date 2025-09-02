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
import mu.KotlinLogging
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class VerifyAjaxResponse(
    val success: Boolean,
    val message: String? = null,
    val verificationResponse: com.external.verification.models.VerificationResponse? = null,
    val error: String? = null
)


fun Route.webRoutes(
    thirdPartyApiService: ThirdPartyApiService,
    jwtStorageService: JwtStorageService,
    basicAuthSessionService: BasicAuthSessionService
) {
    val logger = KotlinLogging.logger {}
    val jsonPrinter = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
    
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
                    call.respondRedirect("/verification-form?username=$username")
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
            "username" to username
        )))
    }
    
    get("/verification-result") {
        try {
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
            
            // In a real implementation, you would pass the actual verification response data
            // For now, we'll pass null to show "N/A" for all fields
            val verificationResponse = null
            
            call.respond(FreeMarkerContent("verification-result.ftl", mapOf(
                "title" to "Verification Result - Verification System",
                "username" to username,
                "verificationResponse" to verificationResponse
            )))
        } catch (e: Exception) {
            logger.error("Error in verification-result route: ${e.message}", e)
            call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.message}")
        }
    }
    
    post("/verify") {
        val formData = call.receiveParameters()
        val username = formData["username"] ?: ""
        
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
            // Temporarily skip JWT check to allow form submission
            // TODO: Re-enable JWT validation when third-party login is working
            val jwt = jwtStorageService.getValidJwt(username)
            // TODO:: uncomment after testing
            /*
            if (jwt == null) {
                call.respondRedirect("/third-party-login?username=$username")
                return@post
            }
            */
            // TODO:: remove effectiveJwt after testing
            // For now, use a placeholder JWT or handle missing JWT gracefully
            val effectiveJwt = jwt ?: "placeholder_jwt_for_testing"
            
            val verificationRequest = com.external.verification.models.VerificationRequest(
                identify = com.external.verification.models.VerificationRequest.Identify(
                    nid10Digit = if (formData["nidType"] == "10digit") formData["nidValue"]?.takeIf { it.isNotBlank() } else null,
                    nid17Digit = if (formData["nidType"] == "17digit") formData["nidValue"]?.takeIf { it.isNotBlank() } else null
                ),
                verify = com.external.verification.models.VerificationRequest.Verify(
                    nameEn = formData["nameEn"]?.takeIf { it.isNotBlank() } ?: "",
                    name = formData["name"]?.takeIf { it.isNotBlank() } ?: "",
                    dateOfBirth = formData["dateOfBirth"]?.takeIf { it.isNotBlank() } ?: "",
                    father = formData["father"]?.takeIf { it.isNotBlank() } ?: "",
                    mother = formData["mother"]?.takeIf { it.isNotBlank() } ?: "",
                    spouse = formData["spouse"]?.takeIf { it.isNotBlank() } ?: "",
                    permanentAddress = com.external.verification.models.VerificationRequest.Address(
                        division = formData["permanentDivision"]?.takeIf { it.isNotBlank() } ?: "",
                        district = formData["permanentDistrict"]?.takeIf { it.isNotBlank() } ?: "",
                        upozila = formData["permanentUpazila"]?.takeIf { it.isNotBlank() } ?: ""
                    ),
                    presentAddress = com.external.verification.models.VerificationRequest.Address(
                        division = formData["presentDivision"]?.takeIf { it.isNotBlank() } ?: "",
                        district = formData["presentDistrict"]?.takeIf { it.isNotBlank() } ?: "",
                        upozila = formData["presentUpazila"]?.takeIf { it.isNotBlank() } ?: ""
                    )
                )
            )
            
            val verificationResponse = thirdPartyApiService.verifyPerson(verificationRequest, effectiveJwt)
            
            // Check if this is an AJAX request
            val isAjax = call.request.headers["X-Requested-With"] == "XMLHttpRequest"
            
            if (isAjax) {
                // Return JSON response for AJAX requests using typed DTO
                val responseDto = VerifyAjaxResponse(
                    success = true,
                    message = "Verification completed successfully",
                    verificationResponse = verificationResponse
                )
                logger.info { "=== APP: VERIFY AJAX SUCCESS ===" }
                logger.info { "AJAX Response Body:\n${jsonPrinter.encodeToString(responseDto)}" }
                call.respond(responseDto)
            } else {
                // Return HTML response for regular form submissions
                            call.respond(FreeMarkerContent("verification-result.ftl", mapOf(
                "title" to "Verification Result - Verification System",
                "username" to username,
                "verificationResponse" to verificationResponse
            )))
            }
            
        } catch (e: Exception) {
            // Decide status and message to propagate, showing 503 explicitly if applicable
            val (responseStatus, errorMessage) = when {
                e is com.external.verification.services.ThirdPartyApiServiceImpl.ThirdPartyHttpException -> {
                    logger.info { "=== APP: VERIFY THIRD-PARTY ERROR PASSTHROUGH ===" }
                    logger.info { "Third-party HTTP Status: ${e.status}" }
                    logger.info { "Third-party Raw Body (exact):\n${e.responseBody}" }
                    val status = try { io.ktor.http.HttpStatusCode.fromValue(e.status) } catch (_: Throwable) { HttpStatusCode.BadGateway }
                    val message = if (e.status == 503) "503 Service Unavailable" else "Third party error ${e.status}"
                    status to message
                }
                e.message?.contains("503") == true || e.message?.contains("Service Unavailable", ignoreCase = true) == true ->
                    HttpStatusCode.ServiceUnavailable to "503 Service Unavailable"
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    HttpStatusCode.InternalServerError to "Third party service not available"
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    HttpStatusCode.Unauthorized to "Authentication failed. Please login to third-party service again."
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    HttpStatusCode.BadRequest to "Invalid verification data. Please check your information and try again."
                else -> HttpStatusCode.BadGateway to "Verification service temporarily unavailable. Please try again later."
            }
            
            // Check if this is an AJAX request
            val isAjax = call.request.headers["X-Requested-With"] == "XMLHttpRequest"
            
            if (isAjax) {
                // Return JSON response for AJAX requests using typed DTO, with accurate status (propagate 503)
                val responseDto = VerifyAjaxResponse(
                    success = false,
                    error = errorMessage
                )
                logger.info { "=== APP: VERIFY AJAX ERROR ===" }
                logger.info { "AJAX Response Body:\n${jsonPrinter.encodeToString(responseDto)}" }
                call.respond(responseStatus, responseDto)
            } else {
                // Return HTML response for regular form submissions
                                 call.respond(FreeMarkerContent("verification-form.ftl", mapOf(
                     "title" to "Person Verification - Verification System",
                     "username" to username,
                     "error" to errorMessage
                 )))
            }
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
