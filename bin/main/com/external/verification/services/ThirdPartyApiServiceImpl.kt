package com.external.verification.services

import com.external.verification.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ThirdPartyApiServiceImpl(
    private val authUrl: String,
    private val verificationUrl: String,
    private val billingUrl: String
) : ThirdPartyApiService {
    
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
    
    override suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return client.post("$authUrl/login") {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }.body()
    }
    
    override suspend fun logout(accessToken: String): String {
        return client.post("$authUrl/logout") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
        }.body()
    }
    
    override suspend fun verifyPerson(
        verificationRequest: VerificationRequest, 
        accessToken: String
    ): VerificationResponse {
        return client.post(verificationUrl) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
            setBody(verificationRequest)
        }.body()
    }
    
    override suspend fun changePassword(
        changePasswordRequest: ChangePasswordRequest, 
        accessToken: String
    ): String {
        return client.post("$authUrl/change-user-password") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
            setBody(changePasswordRequest)
        }.body()
    }
    
    override suspend fun getBillingReport(
        billingRequest: BillingRequest, 
        accessToken: String
    ): BillingResponse {
        return client.post("$billingUrl/get-billing-report") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
            setBody(billingRequest)
        }.body()
    }
}
