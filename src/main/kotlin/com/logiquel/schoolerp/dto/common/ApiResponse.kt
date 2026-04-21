package com.logiquel.schoolerp.dto.common

data class ApiResponse<T>(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: T? = null,
    val error: ApiError? = null
)

data class ApiError(
    val code: String,
    val details: List<String>
)