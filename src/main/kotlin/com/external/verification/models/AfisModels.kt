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

// Enhanced AFIS Verification Request with Capture
@Serializable
data class AfisVerificationWithCaptureRequest(
    val dateOfBirth: String,
    val nid10Digit: String? = null,
    val nid17Digit: String? = null,
    val fingerEnums: List<String>,
    val qualityThreshold: Int? = 70,
    val captureTimeout: Int? = 30000,
    val retryCount: Int? = 3
)

// AFIS Fingerprint Capture Request
@Serializable
data class AfisFingerprintCaptureRequest(
    val fingerEnums: List<String>,
    val qualityThreshold: Int? = 70,
    val captureTimeout: Int? = 60000,
    val retryCount: Int? = 2
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

// Enhanced AFIS Verification Response with Capture Summary
@Serializable
data class AfisVerificationWithCaptureResponse(
    val afisVerification: AfisVerificationResponse,
    val fingerprintCapture: FingerprintCaptureSummary
)

// Fingerprint Capture Summary for AFIS
@Serializable
data class FingerprintCaptureSummary(
    val totalRequested: Int,
    val successfullyCaptured: Int,
    val failedFingers: List<String>,
    val capturedFingerprints: List<CapturedFingerInfo>
)

// Captured Finger Information for AFIS
@Serializable
data class CapturedFingerInfo(
    val fingerType: String,
    val qualityScore: Int?,
    val hasImageData: Boolean,
    val hasWsqData: Boolean,
    val captureTime: String?
)

// AFIS Fingerprint Capture Response
@Serializable
data class AfisFingerprintCaptureResponse(
    val success: Boolean,
    val capturedFingerprints: List<CapturedFingerInfo>,
    val failedFingers: List<String>,
    val totalTime: Long?,
    val qualityThreshold: Int
)

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
