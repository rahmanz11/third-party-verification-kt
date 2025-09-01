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
    val statusCode: String? = null,
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
            val nameEn: String? = null,
            val permanentAddress: PermanentAddress? = null,
            val photo: String? = null,
            val voterArea: String? = null,
            val mobile: String? = null,
            val noFingerprint: Int? = null,
            val nidMother: String? = null
        )
        
        @Serializable
        data class PermanentAddress(
            val division: String? = null,
            val district: String? = null,
            val rmo: String? = null,
            val upozila: String? = null,
            val cityCorporationOrMunicipality: String? = null,
            val unionOrWard: String? = null,
            val postOffice: String? = null,
            val postalCode: String? = null,
            val wardForUnionPorishod: Int? = null,
            val additionalMouzaOrMoholla: String? = null,
            val additionalVillageOrRoad: String? = null,
            val homeOrHoldingNo: String? = null,
            val region: String? = null
        )
    }
    
    @Serializable
    data class FieldVerificationResult(
        val dateOfBirth: Boolean? = null,
        val nameEn: Boolean? = null,
        val name: Boolean? = null,
        val father: Boolean? = null,
        val mother: Boolean? = null
    )
    
    @Serializable
    data class PartialResponse(
        val requestId: String,
        val nationalId: String,
        val pin: String,
        val photo: String
    )
}
