package com.logiquel.schoolerp.controller.v2

import com.logiquel.schoolerp.dto.common.ApiError
import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v2.TenantBillingResponseV2
import com.logiquel.schoolerp.service.v2.CreateTenantBillingRequest
import com.logiquel.schoolerp.service.v2.TenantBillingV2Service
import com.logiquel.schoolerp.service.v2.UpdateTenantBillingRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v2/billing")
class TenantBillingV2Controller(
    private val tenantBillingV2Service: TenantBillingV2Service
) {

    // GET /api/v2/billing
    @GetMapping
    fun findAll(): ResponseEntity<ApiResponse<List<TenantBillingResponseV2>>> {
        return try {
            val response = tenantBillingV2Service.findAll()
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to fetch billing records",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // GET /api/v2/billing/{id}
    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<ApiResponse<TenantBillingResponseV2>> {
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid billing ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$id' is not a valid UUID")
                    )
                )
            )
        }

        return try {
            val response = tenantBillingV2Service.findById(uuid)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to fetch billing record",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // GET /api/v2/billing/tenant/{tenantId}
    @GetMapping("/tenant/{tenantId}")
    fun findByTenantId(
        @PathVariable tenantId: String
    ): ResponseEntity<ApiResponse<TenantBillingResponseV2>> {
        val uuid = try {
            UUID.fromString(tenantId)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid tenant ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$tenantId' is not a valid UUID")
                    )
                )
            )
        }

        return try {
            val response = tenantBillingV2Service.findByTenantId(uuid)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to fetch billing record",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // POST /api/v2/billing
    @PostMapping
    fun create(
        @RequestBody(required = false) request: CreateTenantBillingRequest?
    ): ResponseEntity<ApiResponse<TenantBillingResponseV2>> {
        if (request == null) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Request body is required",
                    error = ApiError(
                        code = "MISSING_REQUEST_BODY",
                        details = listOf("Request body cannot be empty")
                    )
                )
            )
        }

        val validationErrors = mutableListOf<String>()
        if (request.billingCycle.isBlank()) {
            validationErrors.add("billingCycle cannot be blank")
        }
        if (request.billingCycle.isNotBlank() &&
            request.billingCycle !in listOf("monthly", "quarterly", "annually")
        ) {
            validationErrors.add("billingCycle must be one of: monthly, quarterly, annually")
        }

        if (validationErrors.isNotEmpty()) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Validation failed",
                    error = ApiError(
                        code = "VALIDATION_ERROR",
                        details = validationErrors
                    )
                )
            )
        }

        return try {
            val response = tenantBillingV2Service.create(request)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to create billing record",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // PUT /api/v2/billing/{id}
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody(required = false) request: UpdateTenantBillingRequest?
    ): ResponseEntity<ApiResponse<TenantBillingResponseV2>> {
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid billing ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$id' is not a valid UUID")
                    )
                )
            )
        }

        if (request == null) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Request body is required",
                    error = ApiError(
                        code = "MISSING_REQUEST_BODY",
                        details = listOf("Request body cannot be empty")
                    )
                )
            )
        }

        if (request.planId == null &&
            request.billingCycle == null &&
            request.activatedAt == null
        ) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "No fields provided to update",
                    error = ApiError(
                        code = "EMPTY_UPDATE",
                        details = listOf("At least one field must be provided for update")
                    )
                )
            )
        }

        if (request.billingCycle != null &&
            request.billingCycle !in listOf("monthly", "quarterly", "annually")
        ) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Validation failed",
                    error = ApiError(
                        code = "VALIDATION_ERROR",
                        details = listOf("billingCycle must be one of: monthly, quarterly, annually")
                    )
                )
            )
        }

        return try {
            val response = tenantBillingV2Service.update(uuid, request)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to update billing record",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }
}