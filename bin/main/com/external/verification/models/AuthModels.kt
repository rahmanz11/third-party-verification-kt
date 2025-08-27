package com.external.verification.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val status: String,
    val statusCode: String,
    val success: SuccessData? = null,
    val error: ErrorData? = null
) {
    @Serializable
    data class SuccessData(
        val data: LoginData
    ) {
        @Serializable
        data class LoginData(
            val username: String,
            val access_token: String,
            val refresh_token: String
        )
    }
    
    @Serializable
    data class ErrorData(
        val field: String,
        val message: String
    )
}

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

@Serializable
data class ApiResponse(
    val status: String,
    val statusCode: String,
    val success: SuccessData? = null,
    val error: ErrorData? = null
) {
    @Serializable
    data class SuccessData(
        val data: String
    )
    
    @Serializable
    data class ErrorData(
        val field: String,
        val message: String
    )
}
