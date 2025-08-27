package com.external.verification.services

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

data class StoredJwt(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val expiresAt: Instant
)

class JwtStorageService {
    
    private val jwtStorage = ConcurrentHashMap<String, StoredJwt>()
    private val mutex = Mutex()
    
    suspend fun storeJwt(username: String, accessToken: String, refreshToken: String, expiresInSeconds: Long = 43200) {
        mutex.withLock {
            val expiresAt = Instant.now().plusSeconds(expiresInSeconds)
            jwtStorage[username] = StoredJwt(
                accessToken = accessToken,
                refreshToken = refreshToken,
                username = username,
                expiresAt = expiresAt
            )
        }
    }
    
    suspend fun getValidJwt(username: String): String? {
        mutex.withLock {
            val storedJwt = jwtStorage[username] ?: return null
            
            if (Instant.now().isAfter(storedJwt.expiresAt)) {
                jwtStorage.remove(username)
                return null
            }
            
            return storedJwt.accessToken
        }
    }
    
    suspend fun removeJwt(username: String) {
        mutex.withLock {
            jwtStorage.remove(username)
        }
    }
    
    suspend fun isJwtValid(username: String): Boolean {
        return getValidJwt(username) != null
    }
    
    suspend fun getStoredUsernames(): List<String> {
        mutex.withLock {
            return jwtStorage.keys.toList()
        }
    }
    
    suspend fun getStoredJwt(username: String): StoredJwt? {
        mutex.withLock {
            val storedJwt = jwtStorage[username] ?: return null
            
            if (Instant.now().isAfter(storedJwt.expiresAt)) {
                jwtStorage.remove(username)
                return null
            }
            
            return storedJwt
        }
    }
    
    suspend fun cleanupExpiredTokens() {
        mutex.withLock {
            val now = Instant.now()
            jwtStorage.entries.removeIf { (_, storedJwt) ->
                now.isAfter(storedJwt.expiresAt)
            }
        }
    }
}
