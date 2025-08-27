package com.external.verification.routes

import com.external.verification.services.GeoDataService
import com.external.verification.services.BasicAuthSessionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.geoRoutes(
    geoDataService: GeoDataService,
    basicAuthSessionService: BasicAuthSessionService
) {
    
    get("/api/geo/divisions") {
        val username = call.request.queryParameters["username"] ?: ""
        
        if (username.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username is required"))
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Authentication required"))
            return@get
        }
        
        try {
            val divisions = geoDataService.getAllDivisions()
            call.respond(HttpStatusCode.OK, com.external.verification.models.GeoResponse(
                success = true,
                data = divisions
            ))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, com.external.verification.models.GeoResponse(
                success = false,
                error = "Failed to load divisions: ${e.message}"
            ))
        }
    }
    
    get("/api/geo/districts") {
        val username = call.request.queryParameters["username"] ?: ""
        val divisionId = call.request.queryParameters["divisionId"] ?: ""
        
        if (username.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username is required"))
            return@get
        }
        
        if (divisionId.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Division ID is required"))
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Authentication required"))
            return@get
        }
        
        try {
            val districts = geoDataService.getDistrictsByDivision(divisionId)
            call.respond(HttpStatusCode.OK, com.external.verification.models.GeoResponse(
                success = true,
                data = districts
            ))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, com.external.verification.models.GeoResponse(
                success = false,
                error = "Failed to load districts: ${e.message}"
            ))
        }
    }
    
    get("/api/geo/upazilas") {
        val username = call.request.queryParameters["username"] ?: ""
        val districtId = call.request.queryParameters["districtId"] ?: ""
        
        if (username.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username is required"))
            return@get
        }
        
        if (districtId.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "District ID is required"))
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Authentication required"))
            return@get
        }
        
        try {
            val upazilas = geoDataService.getUpazilasByDistrict(districtId)
            call.respond(HttpStatusCode.OK, com.external.verification.models.GeoResponse(
                success = true,
                data = upazilas
            ))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, com.external.verification.models.GeoResponse(
                success = false,
                error = "Failed to load upazilas: ${e.message}"
            ))
        }
    }
    
    get("/api/geo/unions") {
        val username = call.request.queryParameters["username"] ?: ""
        val upazilaId = call.request.queryParameters["upazilaId"] ?: ""
        
        if (username.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Username is required"))
            return@get
        }
        
        if (upazilaId.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Upazila ID is required"))
            return@get
        }
        
        val isValidSession = kotlinx.coroutines.runBlocking { basicAuthSessionService.isValidSession(username) }
        if (!isValidSession) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Authentication required"))
            return@get
        }
        
        try {
            val unions = geoDataService.getUnionsByUpazila(upazilaId)
            call.respond(HttpStatusCode.OK, com.external.verification.models.GeoResponse(
                success = true,
                data = unions
            ))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, com.external.verification.models.GeoResponse(
                success = false,
                error = "Failed to load unions: ${e.message}"
            ))
        }
    }
}
