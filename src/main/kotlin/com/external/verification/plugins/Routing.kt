package com.external.verification.plugins

import com.external.verification.routes.*
import com.external.verification.routes.afisRoutes
import com.external.verification.services.ThirdPartyApiService
import com.external.verification.services.ThirdPartyApiServiceImpl
import com.external.verification.services.JwtStorageService
import com.external.verification.services.BasicAuthSessionService
import com.external.verification.services.GeoDataService
import com.external.verification.services.FingerprintDeviceService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {
    val authUrl = environment.config.property("thirdparty.auth-url").getString()
    val verificationUrl = environment.config.property("thirdparty.verification-url").getString()
    val billingUrl = environment.config.property("thirdparty.billing-url").getString()
    val afisVerificationUrl = environment.config.property("thirdparty.afis-verification-url").getString()
    
    val thirdPartyApiService: ThirdPartyApiService = ThirdPartyApiServiceImpl(authUrl, verificationUrl, billingUrl, afisVerificationUrl)
    val jwtStorageService = JwtStorageService()
    val basicAuthSessionService = BasicAuthSessionService()
    val geoDataService = GeoDataService()
    val fingerprintDeviceService = FingerprintDeviceService()
    
    kotlinx.coroutines.runBlocking {
        try {
            geoDataService.initialize()
            log.info("Geographic data service initialized successfully")
        } catch (e: Exception) {
            log.error("Failed to initialize geographic data service: ${e.message}")
            throw e
        }
    }
    
    routing {
        get("/static/{...}") {
            val path = call.request.uri.removePrefix("/static")
            val resource = this::class.java.classLoader.getResource("static$path")
            if (resource != null) {
                call.respondBytes(resource.readBytes())
            } else {
                call.respondText("File not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
        
        // Test route to serve verification-response.json
        get("/verification-response.json") {
            try {
                val file = java.io.File("verification-response.json")
                if (file.exists()) {
                    val content = file.readText()
                    call.respondText(content, contentType = io.ktor.http.ContentType.Application.Json)
                } else {
                    call.respondText("File not found", status = io.ktor.http.HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText("Error reading file: ${e.message}", status = io.ktor.http.HttpStatusCode.InternalServerError)
            }
        }
        
        webRoutes(thirdPartyApiService, jwtStorageService, basicAuthSessionService)
        geoRoutes(geoDataService, basicAuthSessionService)
        
        // Add fingerprint routes
        fingerprintRoutes(fingerprintDeviceService)
        
        route("/partner-service/rest") {
            authRoutes(thirdPartyApiService)
            verificationRoutes(thirdPartyApiService)
            billingRoutes(thirdPartyApiService)
            afisRoutes(thirdPartyApiService)
        }
    }
}
