package com.external.verification.plugins

import com.external.verification.routes.*
import com.external.verification.services.ThirdPartyApiService
 import com.external.verification.services.ThirdPartyApiServiceImpl
import com.external.verification.services.JwtStorageService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    
    val authUrl = environment.config.property("thirdparty.auth-url").getString()
    val verificationUrl = environment.config.property("thirdparty.verification-url").getString()
    val billingUrl = environment.config.property("thirdparty.billing-url").getString()
    
    val thirdPartyApiService: ThirdPartyApiService = ThirdPartyApiServiceImpl(authUrl, verificationUrl, billingUrl)
    val jwtStorageService = JwtStorageService()
    
    routing {
        // Web routes (HTML pages)
        webRoutes(thirdPartyApiService, jwtStorageService)
        
        // API routes (REST endpoints)
        route("/partner-service/rest") {
            authRoutes(thirdPartyApiService)
            verificationRoutes(thirdPartyApiService)
            billingRoutes(thirdPartyApiService)
        }
    }
}
