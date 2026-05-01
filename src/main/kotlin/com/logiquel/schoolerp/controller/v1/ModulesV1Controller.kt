package com.logiquel.schoolerp.controller.v1

import com.logiquel.schoolerp.dto.common.ApiError
import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v1.CreateModuleRequest
import com.logiquel.schoolerp.dto.v1.ModuleResponse
import com.logiquel.schoolerp.dto.v1.UpdateModuleRequest
import com.logiquel.schoolerp.service.v1.ModulesV1Service
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/modules")
class ModulesV1Controller(private val modulesV1Service: ModulesV1Service) {

    // GET /api/v1/modules

    @GetMapping
    fun findAll(): ResponseEntity<ApiResponse<List<ModuleResponse>>> {
        return try {
            val modules = modulesV1Service.findAll()
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    status  = 200,
                    message = "Modules fetched successfully",
                    data    = modules
                )
            )
        } catch (e: Exception) {
            val error = ApiResponse<List<ModuleResponse>>(
                success = false,
                status = 500,
                message = "Failed to fetch modules",
                error = ApiError(
                    code = "INTERNAL_SERVER_ERROR",
                    details = listOf(e.message ?: "Unexpected error occurred")
                )
            )
            ResponseEntity.status(500).body(error)
        }
    }

    // GET /api/v1/modules/{id}
    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<ApiResponse<ModuleResponse>> {
        // edge case — invalid UUID format
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid module ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$id' is not a valid UUID")
                    )
                )
            )
        }

        return try {
            val response = modulesV1Service.findById(uuid)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to fetch module",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // POST /api/v1/modules
    @PostMapping
    fun create(@RequestBody(required = false) request: CreateModuleRequest?): ResponseEntity<ApiResponse<ModuleResponse>> {
        // edge case — empty request body
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
        if (request.key.isBlank()) validationErrors.add("key cannot be blank")
        if (request.name.isBlank()) validationErrors.add("name cannot be blank")

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
            val response = modulesV1Service.create(request)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to create module",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // PUT /api/v1/modules/{id}
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody(required = false) request: UpdateModuleRequest?
    ): ResponseEntity<ApiResponse<ModuleResponse>> {
        // edge case — invalid UUID format
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid module ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$id' is not a valid UUID")
                    )
                )
            )
        }

        // edge case — empty request body
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

        // edge case — all fields are null (nothing to update)
        if (request.name == null &&
            request.description == null &&
            request.icon == null &&
            request.pricePerStudent == null &&
            request.isActive == null &&
            request.sortOrder == null &&
            request.roleId == null
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

        // edge case — blank name if provided
        if (request.name != null && request.name.isBlank()) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Validation failed",
                    error = ApiError(
                        code = "VALIDATION_ERROR",
                        details = listOf("name cannot be blank")
                    )
                )
            )
        }

        return try {
            val response = modulesV1Service.update(uuid, request)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to update module",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // DELETE /api/v1/modules/{id}
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
                    message = "Invalid module ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$id' is not a valid UUID")
                    )
                )
            )
        }

        return try {
            val response = modulesV1Service.delete(uuid)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to delete module",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }
}