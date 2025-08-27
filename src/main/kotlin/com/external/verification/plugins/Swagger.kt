package com.external.verification.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureSwagger() {
    routing {
        swaggerUI(path = "swagger-ui", swaggerFile = "openapi/documentation.yaml")
    }
}
