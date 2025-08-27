package com.external.verification.plugins

import com.external.verification.routes.*
import com.external.verification.services.ThirdPartyApiService
import com.external.verification.services.ThirdPartyApiServiceImpl
import com.external.verification.services.JwtStorageService
import com.external.verification.services.BasicAuthSessionService
import com.external.verification.services.GeoDataService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {
    val authUrl = environment.config.property("thirdparty.auth-url").getString()
    val verificationUrl = environment.config.property("thirdparty.verification-url").getString()
    val billingUrl = environment.config.property("thirdparty.billing-url").getString()
    
    val thirdPartyApiService: ThirdPartyApiService = ThirdPartyApiServiceImpl(authUrl, verificationUrl, billingUrl)
    val jwtStorageService = JwtStorageService()
    val basicAuthSessionService = BasicAuthSessionService()
    val geoDataService = GeoDataService()
    
    // Initialize geo data service
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
        
        webRoutes(thirdPartyApiService, jwtStorageService, basicAuthSessionService)
        geoRoutes(geoDataService, basicAuthSessionService)
        
        route("/partner-service/rest") {
            authRoutes(thirdPartyApiService)
            verificationRoutes(thirdPartyApiService)
            billingRoutes(thirdPartyApiService)
        }
    }
}
