package com.external.verification.models

import kotlinx.serialization.Serializable

// AFIS Error Data (shared between responses)
@Serializable
data class AfisErrorData(
    val field: String,
    val message: String
)

// AFIS Verification Request
@Serializable
data class AfisVerificationRequest(
    val dateOfBirth: String,
    val nid10Digit: String? = null,
    val nid17Digit: String? = null,
    val fingerEnums: List<String>
)

// AFIS Verification Response
@Serializable
data class AfisVerificationResponse(
    val status: String,
    val statusCode: String? = null,
    val success: AfisSuccessData? = null,
    val error: AfisErrorData? = null
) {
    @Serializable
    data class AfisSuccessData(
        val data: AfisData
    ) {
        @Serializable
        data class AfisData(
            val fingerUploadUrls: List<FingerUploadUrl>,
            val resultCheckApi: String
        )
    }
}

// Finger Upload URL
@Serializable
data class FingerUploadUrl(
    val finger: String,
    val url: String
)

// AFIS Result Check Response
@Serializable
data class AfisResultResponse(
    val status: String,
    val statusCode: String? = null,
    val success: AfisResultSuccessData? = null,
    val error: AfisErrorData? = null
) {
    @Serializable
    data class AfisResultSuccessData(
        val data: AfisResultData
    ) {
        @Serializable
        data class AfisResultData(
            val jobId: String,
            val result: String,
            val verificationResponse: AfisVerificationResult? = null,
            val errorReason: String? = null
        )
    }
}

// AFIS Verification Result
@Serializable
data class AfisVerificationResult(
    val voterInfo: VoterInfo
) {
    @Serializable
    data class VoterInfo(
        val id: String,
        val nationalId: String
    )
}

// Fingerprint Upload Response
@Serializable
data class FingerprintUploadResponse(
    val success: Boolean,
    val message: String,
    val statusCode: Int? = null
)
