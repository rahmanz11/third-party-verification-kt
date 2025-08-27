package com.external.verification

import com.external.verification.plugins.*
import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureTemplating()
    configureRouting()
    configureSwagger()
}
