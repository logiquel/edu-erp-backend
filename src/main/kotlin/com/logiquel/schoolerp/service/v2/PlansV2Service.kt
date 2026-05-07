package com.logiquel.schoolerp.service.v2

import com.logiquel.schoolerp.dto.common.ApiError
import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v2.PlanModuleResponse
import com.logiquel.schoolerp.dto.v2.PlanResponse
import com.logiquel.schoolerp.entities.v2.PlanEntityV2
import com.logiquel.schoolerp.entities.v2.PlanModuleEntityV2
import com.logiquel.schoolerp.repo.v2.ModuleRepositoryV2
import com.logiquel.schoolerp.repo.v2.PlanModuleRepositoryV2
import com.logiquel.schoolerp.repo.v2.PlanRepositoryV2
import com.logiquel.schoolerp.repo.v2.TenantRepositoryV2
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CreatePlanRequest(
    val tenantId: UUID,
    val name: String,
    val moduleIds: List<UUID> = emptyList()
)

data class UpdatePlanRequest(
    val name: String? = null
)

data class AddModuleToPlanRequest(
    val moduleId: UUID,
    val pricePerStudent: BigDecimal
)

@Service
class PlansV2Service(
    private val planRepositoryV2: PlanRepositoryV2,
    private val planModuleRepositoryV2: PlanModuleRepositoryV2,
    private val tenantRepositoryV2: TenantRepositoryV2,
    private val moduleRepositoryV2: ModuleRepositoryV2
) {

    // ─────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────
    fun findAll(): ApiResponse<List<PlanResponse>> {
        val plans = planRepositoryV2.findAll().map { it.toResponse() }
        return ApiResponse(
            success = true,
            status = 200,
            message = "Plans fetched successfully",
            data = plans
        )
    }

    // ─────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────
    fun findById(id: UUID): ApiResponse<PlanResponse> {
        val plan = planRepositoryV2.findByIdWithTenant(id).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "Plan not found",
                error = ApiError(
                    code = "PLAN_NOT_FOUND",
                    details = listOf("No plan exists with id: $id")
                )
            )
        return ApiResponse(
            success = true,
            status = 200,
            message = "Plan fetched successfully",
            data = plan.toResponse()
        )
    }

    // ─────────────────────────────────────
    // GET BY TENANT ID
    // ─────────────────────────────────────
    fun findByTenantId(tenantId: UUID): ApiResponse<PlanResponse> {
        val plan = planRepositoryV2.findByTenantId(tenantId).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "No plan found for this tenant",
                error = ApiError(
                    code = "PLAN_NOT_FOUND",
                    details = listOf("No plan exists for tenant id: $tenantId")
                )
            )
        return ApiResponse(
            success = true,
            status = 200,
            message = "Plan fetched successfully",
            data = plan.toResponse()
        )
    }

    // ─────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────
    @Transactional
    fun create(request: CreatePlanRequest): ApiResponse<PlanResponse> {
        val tenant = tenantRepositoryV2.findById(request.tenantId).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "Tenant not found",
                error = ApiError(
                    code = "TENANT_NOT_FOUND",
                    details = listOf("No tenant exists with id: ${request.tenantId}")
                )
            )

        if (planRepositoryV2.existsByTenantId(request.tenantId)) {
            return ApiResponse(
                success = false,
                status = 409,
                message = "Plan already exists for this tenant",
                error = ApiError(
                    code = "PLAN_ALREADY_EXISTS",
                    details = listOf("Tenant ${request.tenantId} already has a plan")
                )
            )
        }

        val plan = PlanEntityV2(
            tenant = tenant,
            name = request.name
        )
        val saved = planRepositoryV2.save(plan)

        // add modules to plan if provided
        if (request.moduleIds.isNotEmpty()) {
            request.moduleIds.forEach { moduleId ->
                val module = moduleRepositoryV2.findById(moduleId).orElse(null)
                if (module != null) {
                    val planModule = PlanModuleEntityV2(
                        plan = saved,
                        module = module,
                        pricePerStudent = module.pricePerStudent
                    )
                    planModuleRepositoryV2.save(planModule)
                }
            }
        }

        return ApiResponse(
            success = true,
            status = 200,
            message = "Plan created successfully",
            data = saved.toResponse()
        )
    }

    // ─────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────
    @Transactional
    fun update(id: UUID, request: UpdatePlanRequest): ApiResponse<PlanResponse> {
        val plan = planRepositoryV2.findById(id).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "Plan not found",
                error = ApiError(
                    code = "PLAN_NOT_FOUND",
                    details = listOf("No plan exists with id: $id")
                )
            )

        request.name?.let { plan.name = it }
        plan.updatedAt = LocalDateTime.now()
        val updated = planRepositoryV2.save(plan)

        return ApiResponse(
            success = true,
            status = 200,
            message = "Plan updated successfully",
            data = updated.toResponse()
        )
    }

    // ─────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────
    @Transactional
    fun delete(id: UUID): ApiResponse<Nothing> {
        if (!planRepositoryV2.existsById(id)) {
            return ApiResponse(
                success = false,
                status = 404,
                message = "Plan not found",
                error = ApiError(
                    code = "PLAN_NOT_FOUND",
                    details = listOf("No plan exists with id: $id")
                )
            )
        }
        planRepositoryV2.deleteById(id)
        return ApiResponse(
            success = true,
            status = 200,
            message = "Plan deleted successfully"
        )
    }

    // ─────────────────────────────────────
    // ADD MODULE TO PLAN
    // ─────────────────────────────────────
    @Transactional
    fun addModule(planId: UUID, request: AddModuleToPlanRequest): ApiResponse<PlanResponse> {
        val plan = planRepositoryV2.findById(planId).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "Plan not found",
                error = ApiError(
                    code = "PLAN_NOT_FOUND",
                    details = listOf("No plan exists with id: $planId")
                )
            )

        val module = moduleRepositoryV2.findById(request.moduleId).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "Module not found",
                error = ApiError(
                    code = "MODULE_NOT_FOUND",
                    details = listOf("No module exists with id: ${request.moduleId}")
                )
            )

        if (planModuleRepositoryV2.existsByPlanIdAndModuleId(planId, request.moduleId)) {
            return ApiResponse(
                success = false,
                status = 409,
                message = "Module already exists in this plan",
                error = ApiError(
                    code = "MODULE_ALREADY_IN_PLAN",
                    details = listOf("Module ${request.moduleId} is already part of plan $planId")
                )
            )
        }

        val planModule = PlanModuleEntityV2(
            plan = plan,
            module = module,
            pricePerStudent = request.pricePerStudent
        )
        planModuleRepositoryV2.save(planModule)
        plan.updatedAt = LocalDateTime.now()
        planRepositoryV2.save(plan)

        return ApiResponse(
            success = true,
            status = 200,
            message = "Module added to plan successfully",
            data = plan.toResponse()
        )
    }

    // ─────────────────────────────────────
    // REMOVE MODULE FROM PLAN
    // ─────────────────────────────────────
    @Transactional
    fun removeModule(planId: UUID, moduleId: UUID): ApiResponse<PlanResponse> {
        if (!planRepositoryV2.existsById(planId)) {
            return ApiResponse(
                success = false,
                status = 404,
                message = "Plan not found",
                error = ApiError(
                    code = "PLAN_NOT_FOUND",
                    details = listOf("No plan exists with id: $planId")
                )
            )
        }

        if (!planModuleRepositoryV2.existsByPlanIdAndModuleId(planId, moduleId)) {
            return ApiResponse(
                success = false,
                status = 404,
                message = "Module not found in this plan",
                error = ApiError(
                    code = "MODULE_NOT_IN_PLAN",
                    details = listOf("Module $moduleId is not part of plan $planId")
                )
            )
        }

        planModuleRepositoryV2.deleteByPlanIdAndModuleId(planId, moduleId)

        val plan = planRepositoryV2.findById(planId).get()
        plan.updatedAt = LocalDateTime.now()
        planRepositoryV2.save(plan)

        return ApiResponse(
            success = true,
            status = 200,
            message = "Module removed from plan successfully",
            data = plan.toResponse()
        )
    }

    // ─────────────────────────────────────
    // Mapper
    // ─────────────────────────────────────
    private fun PlanEntityV2.toResponse(): PlanResponse {
        val modules = planModuleRepositoryV2.findAllByPlanIdWithModule(id!!)
        return PlanResponse(
            id = id!!,
            tenantId = tenant.id!!,
            tenantName = tenant.name,
            name = name,
            modules = modules.map { it.toModuleResponse() },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun PlanModuleEntityV2.toModuleResponse() = PlanModuleResponse(
        id = id!!,
        moduleId = module.id!!,
        moduleKey = module.key,
        moduleName = module.name,
        pricePerStudent = pricePerStudent,
        createdAt = createdAt
    )
}