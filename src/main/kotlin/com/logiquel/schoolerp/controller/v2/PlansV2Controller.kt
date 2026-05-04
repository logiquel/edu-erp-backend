package com.logiquel.schoolerp.controller.v2

import com.logiquel.schoolerp.dto.common.ApiError
import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v2.PlanResponse
import com.logiquel.schoolerp.service.v2.AddModuleToPlanRequest
import com.logiquel.schoolerp.service.v2.CreatePlanRequest
import com.logiquel.schoolerp.service.v2.PlansV2Service
import com.logiquel.schoolerp.service.v2.UpdatePlanRequest
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
@RequestMapping("/api/v2/plans")
class PlansV2Controller(private val plansV2Service: PlansV2Service) {

    // GET /api/v2/plans
    @GetMapping
    fun findAll(): ResponseEntity<ApiResponse<List<PlanResponse>>> {
        return try {
            val response = plansV2Service.findAll()
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to fetch plans",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // GET /api/v2/plans/{id}
    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<ApiResponse<PlanResponse>> {
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid plan ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$id' is not a valid UUID")
                    )
                )
            )
        }

        return try {
            val response = plansV2Service.findById(uuid)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to fetch plan",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // GET /api/v2/plans/tenant/{tenantId}
    @GetMapping("/tenant/{tenantId}")
    fun findByTenantId(@PathVariable tenantId: String): ResponseEntity<ApiResponse<PlanResponse>> {
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
            val response = plansV2Service.findByTenantId(uuid)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to fetch plan",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // POST /api/v2/plans
    @PostMapping
    fun create(
        @RequestBody(required = false) request: CreatePlanRequest?
    ): ResponseEntity<ApiResponse<PlanResponse>> {
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
            val response = plansV2Service.create(request)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to create plan",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // PUT /api/v2/plans/{id}
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody(required = false) request: UpdatePlanRequest?
    ): ResponseEntity<ApiResponse<PlanResponse>> {
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid plan ID format",
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

        if (request.name == null) {
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

        if (request.name.isBlank()) {
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
            val response = plansV2Service.update(uuid, request)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to update plan",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // DELETE /api/v2/plans/{id}
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid plan ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$id' is not a valid UUID")
                    )
                )
            )
        }

        return try {
            val response = plansV2Service.delete(uuid)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to delete plan",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // POST /api/v2/plans/{id}/modules
    @PostMapping("/{id}/modules")
    fun addModule(
        @PathVariable id: String,
        @RequestBody(required = false) request: AddModuleToPlanRequest?
    ): ResponseEntity<ApiResponse<PlanResponse>> {
        val uuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid plan ID format",
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

        if (request.pricePerStudent < java.math.BigDecimal.ZERO) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Validation failed",
                    error = ApiError(
                        code = "VALIDATION_ERROR",
                        details = listOf("pricePerStudent cannot be negative")
                    )
                )
            )
        }

        return try {
            val response = plansV2Service.addModule(uuid, request)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to add module to plan",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }

    // DELETE /api/v2/plans/{id}/modules/{moduleId}
    @DeleteMapping("/{id}/modules/{moduleId}")
    fun removeModule(
        @PathVariable id: String,
        @PathVariable moduleId: String
    ): ResponseEntity<ApiResponse<PlanResponse>> {
        val planUuid = try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid plan ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$id' is not a valid UUID")
                    )
                )
            )
        }

        val moduleUuid = try {
            UUID.fromString(moduleId)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(400).body(
                ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid module ID format",
                    error = ApiError(
                        code = "INVALID_UUID",
                        details = listOf("'$moduleId' is not a valid UUID")
                    )
                )
            )
        }

        return try {
            val response = plansV2Service.removeModule(planUuid, moduleUuid)
            ResponseEntity.status(response.status).body(response)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(
                ApiResponse(
                    success = false,
                    status = 500,
                    message = "Failed to remove module from plan",
                    error = ApiError(
                        code = "INTERNAL_SERVER_ERROR",
                        details = listOf(e.message ?: "Unexpected error occurred")
                    )
                )
            )
        }
    }
}