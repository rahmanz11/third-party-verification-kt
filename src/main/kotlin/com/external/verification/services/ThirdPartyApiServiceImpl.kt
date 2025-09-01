package com.external.verification.services

import com.external.verification.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlinx.serialization.encodeToString
import mu.KotlinLogging

class ThirdPartyApiServiceImpl(
    private val authUrl: String,
    private val verificationUrl: String,
    private val billingUrl: String,
    private val afisVerificationUrl: String
) : ThirdPartyApiService {
    
    private val logger = KotlinLogging.logger {}
    private val apiLogger = ThirdPartyApiLogger()
    private val jsonPrinter = Json { 
        prettyPrint = true 
        isLenient = true 
        ignoreUnknownKeys = true 
    }
    
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
        val startTime = System.currentTimeMillis()
        
        logger.info { "=== THIRD PARTY API: LOGIN REQUEST ===" }
        logger.info { "URL: $authUrl" }
        logger.info { "Request Body:\n${jsonPrinter.encodeToString(loginRequest)}" }
        
        apiLogger.logRequest("LOGIN", "$authUrl", loginRequest)
        
        try {
            val response: LoginResponse = client.post("$authUrl") {
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
            }.body()
            
            val duration = System.currentTimeMillis() - startTime
            
            logger.info { "=== THIRD PARTY API: LOGIN RESPONSE ===" }
            logger.info { "Response Body:\n${jsonPrinter.encodeToString(response)}" }
            
            apiLogger.logResponse("LOGIN", response, duration)
            
            return response
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            apiLogger.logError("LOGIN", e, duration)
            
            when {
                e.message?.contains("503") == true || e.message?.contains("Service Unavailable") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    throw Exception("500 Internal Server Error")
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    throw Exception("401 Unauthorized")
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    throw Exception("400 Bad Request")
                e.message?.contains("NoTransformationFoundException") == true || 
                e.message?.contains("ContentType") == true || 
                e.message?.contains("text/html") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("Connection") == true || 
                e.message?.contains("timeout") == true || 
                e.message?.contains("ConnectException") == true -> 
                    throw Exception("503 Service Unavailable")
                else -> throw Exception("503 Service Unavailable")
            }
        }
    }
    
    override suspend fun logout(accessToken: String): LogoutResponse {
        val startTime = System.currentTimeMillis()
        
        val logoutUrl = authUrl.replace("/login", "/logout")
        
        logger.info { "=== THIRD PARTY API: LOGOUT REQUEST ===" }
        logger.info { "URL: $logoutUrl" }
        logger.info { "Authorization: Bearer ${accessToken.take(10)}..." }
        
        apiLogger.logRequest("LOGOUT", logoutUrl, "{}", accessToken)
        
        try {
            val response: LogoutResponse = client.post(logoutUrl) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $accessToken")
            }.body()
            
            val duration = System.currentTimeMillis() - startTime
            
            logger.info { "=== THIRD PARTY API: LOGOUT RESPONSE ===" }
            logger.info { "Response Body:\n${jsonPrinter.encodeToString(response)}" }
            
            apiLogger.logResponse("LOGOUT", response, duration)
            
            return response
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            apiLogger.logError("LOGOUT", e, duration)
            
            when {
                e.message?.contains("503") == true || e.message?.contains("Service Unavailable") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    throw Exception("500 Internal Server Error")
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    throw Exception("401 Unauthorized")
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    throw Exception("400 Bad Request")
                e.message?.contains("NoTransformationFoundException") == true || 
                e.message?.contains("ContentType") == true || 
                e.message?.contains("text/html") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("Connection") == true || 
                e.message?.contains("timeout") == true || 
                e.message?.contains("ConnectException") == true -> 
                    throw Exception("503 Service Unavailable")
                else -> throw Exception("503 Service Unavailable")
            }
        }
    }
    
    class ThirdPartyHttpException(val status: Int, val responseBody: String) : Exception("HTTP $status from third party")

    override suspend fun verifyPerson(
        verificationRequest: VerificationRequest, 
        accessToken: String
    ): VerificationResponse {
        val startTime = System.currentTimeMillis()
        
        logger.info { "=== THIRD PARTY API: VERIFY PERSON REQUEST ===" }
        logger.info { "URL: $verificationUrl" }
        logger.info { "Authorization: Bearer ${accessToken.take(10)}..." }

        // Build pruned JSON (omit blanks/nulls) for logging and request
        val permanentAddressJson = buildJsonObject {
            verificationRequest.verify.permanentAddress.division.takeIf { it.isNotBlank() }?.let { put("division", it) }
            verificationRequest.verify.permanentAddress.district.takeIf { it.isNotBlank() }?.let { put("district", it) }
            verificationRequest.verify.permanentAddress.upozila.takeIf { it.isNotBlank() }?.let { put("upozila", it) }
        }
        val presentAddressJson = buildJsonObject {
            verificationRequest.verify.presentAddress.division.takeIf { it.isNotBlank() }?.let { put("division", it) }
            verificationRequest.verify.presentAddress.district.takeIf { it.isNotBlank() }?.let { put("district", it) }
            verificationRequest.verify.presentAddress.upozila.takeIf { it.isNotBlank() }?.let { put("upozila", it) }
        }

        val prunedJson = buildJsonObject {
            putJsonObject("identify") {
                verificationRequest.identify.nid10Digit?.takeIf { it.isNotBlank() }?.let { put("nid10Digit", it) }
                verificationRequest.identify.nid17Digit?.takeIf { it.isNotBlank() }?.let { put("nid17Digit", it) }
            }
            putJsonObject("verify") {
                verificationRequest.verify.nameEn.takeIf { it.isNotBlank() }?.let { put("nameEn", it) }
                verificationRequest.verify.name.takeIf { it.isNotBlank() }?.let { put("name", it) }
                verificationRequest.verify.dateOfBirth.takeIf { it.isNotBlank() }?.let { put("dateOfBirth", it) }
                verificationRequest.verify.father.takeIf { it.isNotBlank() }?.let { put("father", it) }
                verificationRequest.verify.mother.takeIf { it.isNotBlank() }?.let { put("mother", it) }
                verificationRequest.verify.spouse.takeIf { it.isNotBlank() }?.let { put("spouse", it) }
                if (permanentAddressJson.isNotEmpty()) {
                    put("permanentAddress", permanentAddressJson)
                }
                if (presentAddressJson.isNotEmpty()) {
                    put("presentAddress", presentAddressJson)
                }
            }
        }

        logger.info { "Request Body (pruned):\n${jsonPrinter.encodeToString(prunedJson)}" }
        
        apiLogger.logRequest("VERIFY_PERSON", verificationUrl, prunedJson, accessToken)
        
        try {
            val httpResponse: HttpResponse = client.post("$verificationUrl") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $accessToken")
                setBody(prunedJson)
            }

            val rawBody: String = httpResponse.bodyAsText()

            val duration = System.currentTimeMillis() - startTime

            logger.info { "=== THIRD PARTY API: VERIFY PERSON RESPONSE ===" }
            logger.info { "Status: ${httpResponse.status.value}" }
            logger.info { "Raw Body (exact):\n$rawBody" }

            if (httpResponse.status.isSuccess()) {
                try {
                    val parsed: VerificationResponse = Json { ignoreUnknownKeys = true }.decodeFromString(VerificationResponse.serializer(), rawBody)
                    logger.info { "Parsed Body:\n${jsonPrinter.encodeToString(parsed)}" }
                    apiLogger.logResponse("VERIFY_PERSON", parsed, duration)
                    return parsed
                } catch (parseException: Exception) {
                    logger.error { "=== JSON PARSING ERROR ===" }
                    logger.error { "Parse Exception: ${parseException.message}" }
                    logger.error { "Parse Exception Type: ${parseException.javaClass.simpleName}" }
                    logger.error { "Raw Response Body: $rawBody" }
                    throw parseException
                }
            } else {
                apiLogger.logError("VERIFY_PERSON", Exception("HTTP ${httpResponse.status.value}"), duration)
                throw ThirdPartyHttpException(httpResponse.status.value, rawBody)
            }
        } catch (e: ThirdPartyHttpException) {
            // Already logged raw body above; rethrow to be handled by caller
            throw e
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            apiLogger.logError("VERIFY_PERSON", e, duration)
            
            when {
                e.message?.contains("503") == true || e.message?.contains("Service Unavailable") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    throw Exception("500 Internal Server Error")
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    throw Exception("401 Unauthorized")
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    throw Exception("400 Bad Request")
                e.message?.contains("NoTransformationFoundException") == true || 
                e.message?.contains("ContentType") == true || 
                e.message?.contains("text/html") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("Connection") == true || 
                e.message?.contains("timeout") == true || 
                e.message?.contains("ConnectException") == true -> 
                    throw Exception("503 Service Unavailable")
                else -> throw Exception("503 Service Unavailable")
            }
        }
    }
    
    override suspend fun changePassword(
        changePasswordRequest: ChangePasswordRequest, 
        accessToken: String
    ): String {
        val startTime = System.currentTimeMillis()
        
        logger.info { "=== THIRD PARTY API: CHANGE PASSWORD REQUEST ===" }
        logger.info { "URL: $authUrl" }
        logger.info { "Authorization: Bearer ${accessToken.take(10)}..." }
        logger.info { "Request Body:\n${jsonPrinter.encodeToString(changePasswordRequest)}" }
        
        apiLogger.logRequest("CHANGE_PASSWORD", "$authUrl", changePasswordRequest, accessToken)
        
        try {
            val response: String = client.post("$authUrl") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $accessToken")
                setBody(changePasswordRequest)
            }.body()
            
            val duration = System.currentTimeMillis() - startTime
            
            logger.info { "=== THIRD PARTY API: CHANGE PASSWORD RESPONSE ===" }
            logger.info { "Response Body:\n$response" }
            
            apiLogger.logResponse("CHANGE_PASSWORD", response, duration)
            
            return response
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            apiLogger.logError("CHANGE_PASSWORD", e, duration)
            
            when {
                e.message?.contains("503") == true || e.message?.contains("Service Unavailable") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    throw Exception("500 Internal Server Error")
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    throw Exception("401 Unauthorized")
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    throw Exception("400 Bad Request")
                e.message?.contains("NoTransformationFoundException") == true || 
                e.message?.contains("ContentType") == true || 
                e.message?.contains("text/html") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("Connection") == true || 
                e.message?.contains("timeout") == true || 
                e.message?.contains("ConnectException") == true -> 
                    throw Exception("503 Service Unavailable")
                else -> throw Exception("503 Service Unavailable")
            }
        }
    }
    
    override suspend fun getBillingReport(
        billingRequest: BillingRequest, 
        accessToken: String
    ): BillingResponse {
        val startTime = System.currentTimeMillis()
        
        logger.info { "=== THIRD PARTY API: GET BILLING REPORT REQUEST ===" }
        logger.info { "URL: $billingUrl" }
        logger.info { "Authorization: Bearer ${accessToken.take(10)}..." }
        logger.info { "Request Body:\n${jsonPrinter.encodeToString(billingRequest)}" }
        
        apiLogger.logRequest("GET_BILLING_REPORT", "$billingUrl", billingRequest, accessToken)
        
        try {
            val response: BillingResponse = client.post("$billingUrl") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $accessToken")
                setBody(billingRequest)
            }.body()
            
            val duration = System.currentTimeMillis() - startTime
            
            logger.info { "=== THIRD PARTY API: GET BILLING REPORT RESPONSE ===" }
            logger.info { "Response Body:\n${jsonPrinter.encodeToString(response)}" }
            
            apiLogger.logResponse("GET_BILLING_REPORT", response, duration)
            
            return response
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            apiLogger.logError("GET_BILLING_REPORT", e, duration)
            
            when {
                e.message?.contains("503") == true || e.message?.contains("Service Unavailable") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    throw Exception("500 Internal Server Error")
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    throw Exception("401 Unauthorized")
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    throw Exception("400 Bad Request")
                e.message?.contains("NoTransformationFoundException") == true || 
                e.message?.contains("ContentType") == true || 
                e.message?.contains("text/html") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("Connection") == true || 
                e.message?.contains("timeout") == true || 
                e.message?.contains("ConnectException") == true -> 
                    throw Exception("503 Service Unavailable")
                else -> throw Exception("503 Service Unavailable")
            }
        }
    }
    
    override suspend fun afisVerification(
        afisRequest: AfisVerificationRequest, 
        accessToken: String
    ): AfisVerificationResponse {
        val startTime = System.currentTimeMillis()
        
        logger.info { "=== THIRD PARTY API: AFIS VERIFICATION REQUEST ===" }
        logger.info { "URL: $afisVerificationUrl" }
        logger.info { "Authorization: Bearer ${accessToken.take(10)}..." }
        logger.info { "Request Body:\n${jsonPrinter.encodeToString(afisRequest)}" }
        
        apiLogger.logRequest("AFIS_VERIFICATION", afisVerificationUrl, afisRequest, accessToken)
        
        try {
            val response: AfisVerificationResponse = client.post(afisVerificationUrl) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $accessToken")
                setBody(afisRequest)
            }.body()
            
            val duration = System.currentTimeMillis() - startTime
            
            logger.info { "=== THIRD PARTY API: AFIS VERIFICATION RESPONSE ===" }
            logger.info { "Response Body:\n${jsonPrinter.encodeToString(response)}" }
            
            apiLogger.logResponse("AFIS_VERIFICATION", response, duration)
            
            return response
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            apiLogger.logError("AFIS_VERIFICATION", e, duration)
            
            when {
                e.message?.contains("503") == true || e.message?.contains("Service Unavailable") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    throw Exception("500 Internal Server Error")
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    throw Exception("401 Unauthorized")
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    throw Exception("400 Bad Request")
                e.message?.contains("403") == true || e.message?.contains("Forbidden") == true -> 
                    throw Exception("403 Forbidden")
                e.message?.contains("NoTransformationFoundException") == true || 
                e.message?.contains("ContentType") == true || 
                e.message?.contains("text/html") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("Connection") == true || 
                e.message?.contains("timeout") == true || 
                e.message?.contains("ConnectException") == true -> 
                    throw Exception("503 Service Unavailable")
                else -> throw Exception("503 Service Unavailable")
            }
        }
    }
    
    override suspend fun uploadFingerprint(
        fingerprintUrl: String, 
        fingerprintData: ByteArray
    ): FingerprintUploadResponse {
        val startTime = System.currentTimeMillis()
        
        logger.info { "=== THIRD PARTY API: FINGERPRINT UPLOAD REQUEST ===" }
        logger.info { "URL: $fingerprintUrl" }
        logger.info { "Data Size: ${fingerprintData.size} bytes" }
        
        apiLogger.logRequest("FINGERPRINT_UPLOAD", fingerprintUrl, "Binary data", null)
        
        try {
            val response = client.put(fingerprintUrl) {
                contentType(ContentType.Application.OctetStream)
                setBody(fingerprintData)
            }
            
            val duration = System.currentTimeMillis() - startTime
            
            logger.info { "=== THIRD PARTY API: FINGERPRINT UPLOAD RESPONSE ===" }
            logger.info { "Status: ${response.status}" }
            
            apiLogger.logResponse("FINGERPRINT_UPLOAD", "Status: ${response.status}", duration)
            
            return if (response.status.value in 200..299) {
                FingerprintUploadResponse(
                    success = true,
                    message = "Fingerprint uploaded successfully",
                    statusCode = response.status.value
                )
            } else {
                FingerprintUploadResponse(
                    success = false,
                    message = "Fingerprint upload failed with status: ${response.status.value}",
                    statusCode = response.status.value
                )
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            apiLogger.logError("FINGERPRINT_UPLOAD", e, duration)
            
            when {
                e.message?.contains("503") == true || e.message?.contains("Service Unavailable") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    throw Exception("500 Internal Server Error")
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    throw Exception("401 Unauthorized")
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    throw Exception("400 Bad Request")
                e.message?.contains("Connection") == true || 
                e.message?.contains("timeout") == true || 
                e.message?.contains("ConnectException") == true -> 
                    throw Exception("503 Service Unavailable")
                else -> throw Exception("503 Service Unavailable")
            }
        }
    }
    
    override suspend fun checkAfisResult(
        resultCheckUrl: String, 
        accessToken: String
    ): AfisResultResponse {
        val startTime = System.currentTimeMillis()
        
        logger.info { "=== THIRD PARTY API: AFIS RESULT CHECK REQUEST ===" }
        logger.info { "URL: $resultCheckUrl" }
        logger.info { "Authorization: Bearer ${accessToken.take(10)}..." }
        
        apiLogger.logRequest("AFIS_RESULT_CHECK", resultCheckUrl, "{}", accessToken)
        
        try {
            val response: AfisResultResponse = client.get(resultCheckUrl) {
                header("Authorization", "Bearer $accessToken")
            }.body()
            
            val duration = System.currentTimeMillis() - startTime
            
            logger.info { "=== THIRD PARTY API: AFIS RESULT CHECK RESPONSE ===" }
            logger.info { "Response Body:\n${jsonPrinter.encodeToString(response)}" }
            
            apiLogger.logResponse("AFIS_RESULT_CHECK", response, duration)
            
            return response
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            apiLogger.logError("AFIS_RESULT_CHECK", e, duration)
            
            when {
                e.message?.contains("503") == true || e.message?.contains("Service Unavailable") == true -> 
                    throw Exception("503 Service Unavailable")
                e.message?.contains("500") == true || e.message?.contains("Internal Server Error") == true -> 
                    throw Exception("500 Internal Server Error")
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    throw Exception("401 Unauthorized")
                e.message?.contains("400") == true || e.message?.contains("Bad Request") == true -> 
                    throw Exception("400 Bad Request")
                e.message?.contains("404") == true || e.message?.contains("Not Found") == true -> 
                    throw Exception("404 Not Found")
                e.message?.contains("Connection") == true || 
                e.message?.contains("timeout") == true || 
                e.message?.contains("ConnectException") == true -> 
                    throw Exception("503 Service Unavailable")
                else -> throw Exception("503 Service Unavailable")
            }
        }
    }
}
