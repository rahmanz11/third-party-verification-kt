package com.external.verification.models

import kotlinx.serialization.Serializable

@Serializable
data class BillingRequest(
    val startDate: String,
    val endDate: String
)

@Serializable
data class BillingResponse(
    val status: String,
    val statusCode: String? = null,
    val success: SuccessData? = null
) {
    @Serializable
    data class SuccessData(
        val data: BillingData
    ) {
        @Serializable
        data class BillingData(
            val generatedTime: String,
            val partnerId: Int,
            val totalCallCount: Int,
            val totalSuccessCount: Int,
            val totalFailedCount: Int,
            val totalProcessingCount: Int,
            val totalBill: Double,
            val partnerBillingBeanList: List<PartnerBillingBean>
        )
        
        @Serializable
        data class PartnerBillingBean(
            val partnerId: Int,
            val partnerName: String,
            val username: String,
            val callCount: Int,
            val successCount: Int,
            val failedCount: Int,
            val processingCount: Int,
            val bill: Double
        )
    }
}
