package com.external.verification.services

import com.external.verification.models.*

interface ThirdPartyApiService {
    suspend fun login(loginRequest: LoginRequest): LoginResponse
    suspend fun verifyPerson(verificationRequest: VerificationRequest, accessToken: String): VerificationResponse
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest, accessToken: String): String
    suspend fun getBillingReport(billingRequest: BillingRequest, accessToken: String): BillingResponse
}
