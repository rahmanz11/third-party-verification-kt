package com.external.verification.routes

import com.external.verification.services.JwtStorageService
import com.external.verification.services.ThirdPartyApiService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.freemarker.*


fun Route.webRoutes(
    thirdPartyApiService: ThirdPartyApiService,
    jwtStorageService: JwtStorageService
) {
    
    // Login page
    get("/") {
        call.respond(FreeMarkerContent("login.ftl", mapOf(
            "title" to "Login - Verification System"
        )))
    }
    
    // Handle login form submission - Basic app authentication
    post("/login") {
        try {
            val formData = call.receiveParameters()
            val username = formData["username"] ?: ""
            val password = formData["password"] ?: ""
            
            // Validate input
            if (username.isBlank() || password.isBlank()) {
                call.respond(FreeMarkerContent("login.ftl", mapOf(
                    "title" to "Login - Verification System",
                    "error" to "Please provide valid credentials"
                )))
                return@post
            }
            
            // Basic authentication - hard-coded credentials
            if (username == "admin" && password == "admin123") {
                // Redirect to dashboard
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
    
    // Dashboard page
    get("/dashboard") {
        val username = call.request.queryParameters["username"] ?: ""
        
        if (username.isBlank()) {
            call.respondRedirect("/")
            return@get
        }
        
        call.respond(FreeMarkerContent("dashboard.ftl", mapOf(
            "title" to "Dashboard - Verification System",
            "username" to username
        )))
    }
    
    // Third-party login page
    get("/third-party-login") {
        val username = call.request.queryParameters["username"] ?: ""
        
        call.respond(FreeMarkerContent("third-party-login.ftl", mapOf(
            "title" to "Third Party Login - Verification System",
            "username" to username
        )))
    }
    
    // Handle third-party login
    post("/third-party-login") {
        try {
            val formData = call.receiveParameters()
            val username = formData["username"] ?: ""
            val thirdPartyUsername = formData["thirdPartyUsername"] ?: ""
            val thirdPartyPassword = formData["thirdPartyPassword"] ?: ""
            
            // Validate input
            if (thirdPartyUsername.isBlank() || thirdPartyPassword.isBlank()) {
                call.respond(FreeMarkerContent("third-party-login.ftl", mapOf(
                    "title" to "Third Party Login - Verification System",
                    "username" to username,
                    "error" to "Please provide valid credentials"
                )))
                return@post
            }
            
            // Call third-party login
            val loginRequest = com.external.verification.models.LoginRequest(thirdPartyUsername, thirdPartyPassword)
            val loginResponse = thirdPartyApiService.login(loginRequest)
            
            if (loginResponse.status == "OK" && loginResponse.success != null) {
                val accessToken = loginResponse.success.data.access_token
                val refreshToken = loginResponse.success.data.refresh_token
                
                // Store JWT in memory for third-party API calls
                jwtStorageService.storeJwt(thirdPartyUsername, accessToken, refreshToken)
                
                // Show success message and redirect to verification form
                call.respond(FreeMarkerContent("third-party-login.ftl", mapOf(
                    "title" to "Third Party Login - Verification System",
                    "username" to username,
                    "success" to "Third-party authentication successful! You can now proceed to verification.",
                    "showVerificationLink" to true,
                    "thirdPartyUsername" to thirdPartyUsername
                )))
            } else {
                call.respond(FreeMarkerContent("third-party-login.ftl", mapOf(
                    "title" to "Third Party Login - Verification System",
                    "username" to username,
                    "error" to (loginResponse.error?.message ?: "Third-party login failed")
                )))
            }
            
        } catch (e: Exception) {
            val username = call.request.queryParameters["username"] ?: ""
            call.respond(FreeMarkerContent("third-party-login.ftl", mapOf(
                "title" to "Third Party Login - Verification System",
                "username" to username,
                "error" to "Third-party login failed: ${e.message}"
            )))
        }
    }
    
    // Verification form page
    get("/verification-form") {
        val username = call.request.queryParameters["username"] ?: ""
        val thirdPartyUsername = call.request.queryParameters["thirdPartyUsername"] ?: ""
        
        if (username.isBlank() || thirdPartyUsername.isBlank()) {
            call.respondRedirect("/")
            return@get
        }
        
        call.respond(FreeMarkerContent("verification-form.ftl", mapOf(
            "title" to "Person Verification - Verification System",
            "username" to username,
            "thirdPartyUsername" to thirdPartyUsername
        )))
    }
    
    // Handle verification form submission
    post("/verify") {
        val formData = call.receiveParameters()
        val username = formData["username"] ?: ""
        val thirdPartyUsername = formData["thirdPartyUsername"] ?: ""
        
        try {
            // Get JWT from storage
            val jwt = jwtStorageService.getValidJwt(thirdPartyUsername)
            
            if (jwt == null) {
                // JWT expired or not found, redirect to third-party login
                call.respondRedirect("/third-party-login?username=$username")
                return@post
            }
            
            // Build verification request
            val verificationRequest = com.external.verification.models.VerificationRequest(
                identify = com.external.verification.models.VerificationRequest.Identify(
                    nid10Digit = formData["nid10Digit"]?.takeIf { it.isNotBlank() },
                    nid17Digit = formData["nid17Digit"]?.takeIf { it.isNotBlank() }
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
            
            // Call verification API
            val verificationResponse = thirdPartyApiService.verifyPerson(verificationRequest, jwt)
            
            // Show result
            call.respond(FreeMarkerContent("verification-result.ftl", mapOf(
                "title" to "Verification Result - Verification System",
                "username" to username,
                "thirdPartyUsername" to thirdPartyUsername,
                "verificationResponse" to verificationResponse
            )))
            
        } catch (e: Exception) {
            call.respond(FreeMarkerContent("verification-form.ftl", mapOf(
                "title" to "Person Verification - Verification System",
                "username" to username,
                "error" to "Verification failed: ${e.message}"
            )))
        }
    }
    
    // Logout
    get("/logout") {
        val username = call.request.queryParameters["username"] ?: ""
        if (username.isNotBlank()) {
            jwtStorageService.removeJwt(username)
        }
        call.respondRedirect("/")
    }
}
