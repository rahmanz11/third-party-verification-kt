package com.external.verification.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.external.verification.services.ThirdPartyApiService
import com.external.verification.services.ThirdPartyApiServiceImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    
    val jwtSecret = environment.config.propertyOrNull("jwt.secret")?.getString() ?: "default-secret-key"
    val jwtAudience = environment.config.propertyOrNull("jwt.audience")?.getString() ?: "verification-api"
    val jwtIssuer = environment.config.propertyOrNull("jwt.issuer")?.getString() ?: "verification-service"
    
    install(Authentication) {
        basic("auth-basic") {
            realm = "Verification API"
            validate { credentials ->
                if (credentials.name == "admin" && credentials.password == "admin123") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
        
        jwt("auth-jwt") {
            realm = "Verification API"
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
