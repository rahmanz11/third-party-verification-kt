package com.external.verification.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.websocket.*
import kotlin.time.Duration.Companion.seconds


fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
    
    // Install WebSocket support for real-time fingerprint device communication
    install(WebSockets) {
        pingPeriod = 30.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}
