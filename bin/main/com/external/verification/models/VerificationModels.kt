package com.external.verification.models

import kotlinx.serialization.Serializable

@Serializable
data class VerificationRequest(
    val identify: Identify,
    val verify: Verify
) {
    @Serializable
    data class Identify(
        val nid10Digit: String? = null,
        val nid17Digit: String? = null
    )
    
    @Serializable
    data class Verify(
        val nameEn: String,
        val name: String,
        val dateOfBirth: String,
        val father: String,
        val mother: String,
        val spouse: String = "",
        val permanentAddress: Address,
        val presentAddress: Address
    )
    
    @Serializable
    data class Address(
        val division: String,
        val district: String,
        val upozila: String
    )
}

@Serializable
data class VerificationResponse(
    val status: String,
    val statusCode: String,
    val success: SuccessData? = null,
    val verified: Boolean? = null,
    val fieldVerificationResult: FieldVerificationResult? = null,
    val partialResponse: PartialResponse? = null,
    val message: String? = null
) {
    @Serializable
    data class SuccessData(
        val data: VerificationData
    ) {
        @Serializable
        data class VerificationData(
            val requestId: String,
            val nationalId: String,
            val pin: String,
            val photo: String
        )
    }
    
    @Serializable
    data class FieldVerificationResult(
        val nameEn: Boolean,
        val name: Boolean,
        val dateOfBirth: Boolean,
        val father: Boolean,
        val mother: Boolean
    )
    
    @Serializable
    data class PartialResponse(
        val requestId: String,
        val nationalId: String,
        val pin: String,
        val photo: String
    )
}
