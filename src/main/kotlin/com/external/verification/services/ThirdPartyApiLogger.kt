package com.external.verification.services

import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ThirdPartyApiLogger {
    
    private val logFile = File("logs/application.log")
    private val jsonPrinter = Json { 
        prettyPrint = true 
        isLenient = true 
        ignoreUnknownKeys = true 
    }
    
    init {
        logFile.parentFile?.mkdirs()
    }
    
    fun logRequest(apiName: String, url: String, requestBody: Any, authorization: String? = null) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        val logEntry = buildString {
            appendLine("=" * 80)
            appendLine("THIRD PARTY API REQUEST")
            appendLine("=" * 80)
            appendLine("Timestamp: $timestamp")
            appendLine("API: $apiName")
            appendLine("URL: $url")
            if (authorization != null) {
                appendLine("Authorization: Bearer ${authorization.take(10)}...")
            }
            appendLine("Request Body:")
            appendLine(requestBody.toString())
            appendLine()
        }
        
        logFile.appendText(logEntry)
    }
    
    fun logResponse(apiName: String, responseBody: Any, duration: Long? = null) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        val logEntry = buildString {
            appendLine("=" * 80)
            appendLine("THIRD PARTY API RESPONSE")
            appendLine("=" * 80)
            appendLine("Timestamp: $timestamp")
            appendLine("API: $apiName")
            if (duration != null) {
                appendLine("Duration: ${duration}ms")
            }
            appendLine("Response Body:")
            appendLine(responseBody.toString())
            appendLine()
        }
        
        logFile.appendText(logEntry)
    }
    
    fun logError(apiName: String, error: Throwable, duration: Long? = null) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        val logEntry = buildString {
            appendLine("=" * 80)
            appendLine("THIRD PARTY API ERROR")
            appendLine("=" * 80)
            appendLine("Timestamp: $timestamp")
            appendLine("API: $apiName")
            if (duration != null) {
                appendLine("Duration: ${duration}ms")
            }
            appendLine("Error: ${error.javaClass.simpleName}")
            appendLine("Message: ${error.message}")
            appendLine("Stack Trace:")
            error.stackTrace.forEach { 
                appendLine("  $it") 
            }
            appendLine()
        }
        
        logFile.appendText(logEntry)
    }
    
    private operator fun String.times(n: Int): String = repeat(n)
}
