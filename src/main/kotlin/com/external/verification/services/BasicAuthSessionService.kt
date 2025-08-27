package com.external.verification.services

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

data class BasicAuthSession(
    val username: String,
    val expiresAt: Instant
)

class BasicAuthSessionService {
    
    private val sessionStorage = ConcurrentHashMap<String, BasicAuthSession>()
    private val mutex = Mutex()
    
    suspend fun createSession(username: String, expiresInSeconds: Long = 3600) {
        mutex.withLock {
            val expiresAt = Instant.now().plusSeconds(expiresInSeconds)
            sessionStorage[username] = BasicAuthSession(
                username = username,
                expiresAt = expiresAt
            )
        }
    }
    
    suspend fun isValidSession(username: String): Boolean {
        mutex.withLock {
            val session = sessionStorage[username] ?: return false
            
            if (Instant.now().isAfter(session.expiresAt)) {
                sessionStorage.remove(username)
                return false
            }
            
            return true
        }
    }
    
    suspend fun removeSession(username: String) {
        mutex.withLock {
            sessionStorage.remove(username)
        }
    }
    
    suspend fun cleanupExpiredSessions() {
        mutex.withLock {
            val now = Instant.now()
            sessionStorage.entries.removeIf { (_, session) ->
                now.isAfter(session.expiresAt)
            }
        }
    }
}
