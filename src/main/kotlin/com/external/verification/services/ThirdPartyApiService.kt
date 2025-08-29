package com.external.verification.services

import com.external.verification.models.*

interface ThirdPartyApiService {
    suspend fun login(loginRequest: LoginRequest): LoginResponse
    suspend fun logout(accessToken: String): LogoutResponse
    suspend fun verifyPerson(verificationRequest: VerificationRequest, accessToken: String): VerificationResponse
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest, accessToken: String): String
    suspend fun getBillingReport(billingRequest: BillingRequest, accessToken: String): BillingResponse
    
    // AFIS APIs
    suspend fun afisVerification(afisRequest: AfisVerificationRequest, accessToken: String): AfisVerificationResponse
    suspend fun uploadFingerprint(fingerprintUrl: String, fingerprintData: ByteArray): FingerprintUploadResponse
    suspend fun checkAfisResult(resultCheckUrl: String, accessToken: String): AfisResultResponse
}
