package com.logiquel.schoolerp.controller.v2

import com.logiquel.schoolerp.dto.common.ApiError
import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v1.CreateTenantRequest
import com.logiquel.schoolerp.dto.v1.TenantResponse
import com.logiquel.schoolerp.dto.v1.UpdateTenantRequest
import com.logiquel.schoolerp.service.v1.TenantsV1Service
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v2/tenants")
class TenantsV2Controller(private val tenantsV1Service: TenantsV1Service) {

    // GET /api/v1/tenants
    @GetMapping
    fun findAll(): ResponseEntity<ApiResponse<List<TenantResponse>>> {
        return try {
            val response = tenantsV1Service.findAll()
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to fetch tenants",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // GET /api/v1/tenants/{id}
    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<ApiResponse<TenantResponse>> {
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid tenant ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$id' is not a valid UUID")
                    )
                )
            )
        }

        return try {
            val response = tenantsV1Service.findById(uuid)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to fetch tenant",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // GET /api/v1/tenants/slug/{slug}
    @GetMapping("/slug/{slug}")
    fun findBySlug(@PathVariable slug: String): ResponseEntity<ApiResponse<TenantResponse>> {
        // edge case — blank slug
        if (slug.isBlank()) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Slug cannot be blank",
                    error = ApiError(
                        code = "INVALID_SLUG",
                        details = listOf("Slug path variable must not be empty")
                    )
                )
            )
        }

        // edge case — slug has invalid characters (only lowercase letters, numbers, hyphens allowed)
        if (!slug.matches(Regex("^[a-z0-9-]+$"))) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid slug format",
                    error = ApiError(
                        code = "INVALID_SLUG_FORMAT",
                        details = listOf("Slug must contain only lowercase letters, numbers, and hyphens")
                    )
                )
            )
        }

        return try {
            val response = tenantsV1Service.findBySlug(slug)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to fetch tenant",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // POST /api/v1/tenants
    @PostMapping
    fun create(@RequestBody(required = false) request: CreateTenantRequest?): ResponseEntity<ApiResponse<TenantResponse>> {
        // edge case — missing request body
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

        // edge case — blank required fields
        val validationErrors = mutableListOf<String>()
        if (request.slug.isBlank()) validationErrors.add("slug cannot be blank")
        if (request.name.isBlank()) validationErrors.add("name cannot be blank")
        if (request.primaryEmail.isBlank()) validationErrors.add("primaryEmail cannot be blank")

        // edge case — invalid slug format
        if (request.slug.isNotBlank() && !request.slug.matches(Regex("^[a-z0-9-]+$"))) {
            validationErrors.add("slug must contain only lowercase letters, numbers, and hyphens")
        }

        // edge case — invalid email format
        if (request.primaryEmail.isNotBlank() && !request.primaryEmail.matches(Regex("^[^@]+@[^@]+\\.[^@]+$"))) {
            validationErrors.add("primaryEmail is not a valid email address")
        }

        // edge case — invalid established year
        if (request.establishedYear != null) {
            val currentYear = java.time.Year.now().value
            if (request.establishedYear < 1800 || request.establishedYear > currentYear) {
                validationErrors.add("establishedYear must be between 1800 and $currentYear")
            }
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
            val response = tenantsV1Service.create(request)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to create tenant",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // PUT /api/v1/tenants/{id}
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody(required = false) request: UpdateTenantRequest?
    ): ResponseEntity<ApiResponse<TenantResponse>> {
        // edge case — invalid UUID format
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid tenant ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$id' is not a valid UUID")
                    )
                )
            )
        }

        // edge case — missing request body
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

        // edge case — all fields null, nothing to update
        if (request.name == null &&
            request.displayName == null &&
            request.logoUrl == null &&
            request.status == null &&
            request.type == null &&
            request.primaryEmail == null &&
            request.primaryPhone == null &&
            request.website == null &&
            request.addressLine1 == null &&
            request.addressLine2 == null &&
            request.city == null &&
            request.state == null &&
            request.pincode == null &&
            request.country == null &&
            request.boardType == null &&
            request.establishedYear == null
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

        // edge case — validate fields if provided
        val validationErrors = mutableListOf<String>()

        if (request.name != null && request.name.isBlank()) {
            validationErrors.add("name cannot be blank")
        }
        if (request.primaryEmail != null && request.primaryEmail.isBlank()) {
            validationErrors.add("primaryEmail cannot be blank")
        }
        if (request.primaryEmail != null && request.primaryEmail.isNotBlank() &&
            !request.primaryEmail.matches(Regex("^[^@]+@[^@]+\\.[^@]+$"))
        ) {
            validationErrors.add("primaryEmail is not a valid email address")
        }
        if (request.establishedYear != null) {
            val currentYear = java.time.Year.now().value
            if (request.establishedYear < 1800 || request.establishedYear > currentYear) {
                validationErrors.add("establishedYear must be between 1800 and $currentYear")
            }
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
            val response = tenantsV1Service.update(uuid, request)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to update tenant",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // DELETE /api/v1/tenants/{id}
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        // edge case — invalid UUID format
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid tenant ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$id' is not a valid UUID")
                    )
                )
            )
        }

        return try {
            val response = tenantsV1Service.delete(uuid)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to delete tenant",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }
}