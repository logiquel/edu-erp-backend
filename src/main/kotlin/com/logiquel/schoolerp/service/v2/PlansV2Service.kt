package com.logiquel.schoolerp.service.v2

import com.logiquel.schoolerp.dto.common.ApiError
import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v2.PlanModuleResponse
import com.logiquel.schoolerp.dto.v2.PlanResponse
import com.logiquel.schoolerp.entities.v1.PlanEntity
import com.logiquel.schoolerp.entities.v1.PlanModuleEntity
import com.logiquel.schoolerp.repo.ModuleRepository
import com.logiquel.schoolerp.repo.PlanModuleRepository
import com.logiquel.schoolerp.repo.PlanRepository
import com.logiquel.schoolerp.repo.TenantRepository
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
    private val planRepository: PlanRepository,
    private val planModuleRepository: PlanModuleRepository,
    private val tenantRepository: TenantRepository,
    private val moduleRepository: ModuleRepository
) {

    // ─────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────
    fun findAll(): ApiResponse<List<PlanResponse>> {
        val plans = planRepository.findAll().map { it.toResponse() }
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
        val plan = planRepository.findByIdWithTenant(id).orElse(null)
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
        val plan = planRepository.findByTenantId(tenantId).orElse(null)
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
        val tenant = tenantRepository.findById(request.tenantId).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "Tenant not found",
                error = ApiError(
                    code = "TENANT_NOT_FOUND",
                    details = listOf("No tenant exists with id: ${request.tenantId}")
                )
            )

        if (planRepository.existsByTenantId(request.tenantId)) {
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

        val plan = PlanEntity(
            tenant = tenant,
            name = request.name
        )
        val saved = planRepository.save(plan)

        // add modules to plan if provided
        if (request.moduleIds.isNotEmpty()) {
            request.moduleIds.forEach { moduleId ->
                val module = moduleRepository.findById(moduleId).orElse(null)
                if (module != null) {
                    val planModule = PlanModuleEntity(
                        plan = saved,
                        module = module,
                        pricePerStudent = module.pricePerStudent
                    )
                    planModuleRepository.save(planModule)
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
        val plan = planRepository.findById(id).orElse(null)
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
        val updated = planRepository.save(plan)

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
        if (!planRepository.existsById(id)) {
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
        planRepository.deleteById(id)
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
        val plan = planRepository.findById(planId).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "Plan not found",
                error = ApiError(
                    code = "PLAN_NOT_FOUND",
                    details = listOf("No plan exists with id: $planId")
                )
            )

        val module = moduleRepository.findById(request.moduleId).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "Module not found",
                error = ApiError(
                    code = "MODULE_NOT_FOUND",
                    details = listOf("No module exists with id: ${request.moduleId}")
                )
            )

        if (planModuleRepository.existsByPlanIdAndModuleId(planId, request.moduleId)) {
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

        val planModule = PlanModuleEntity(
            plan = plan,
            module = module,
            pricePerStudent = request.pricePerStudent
        )
        planModuleRepository.save(planModule)
        plan.updatedAt = LocalDateTime.now()
        planRepository.save(plan)

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
        if (!planRepository.existsById(planId)) {
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

        if (!planModuleRepository.existsByPlanIdAndModuleId(planId, moduleId)) {
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

        planModuleRepository.deleteByPlanIdAndModuleId(planId, moduleId)

        val plan = planRepository.findById(planId).get()
        plan.updatedAt = LocalDateTime.now()
        planRepository.save(plan)

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
    private fun PlanEntity.toResponse(): PlanResponse {
        val modules = planModuleRepository.findAllByPlanIdWithModule(id!!)
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

    private fun PlanModuleEntity.toModuleResponse() = PlanModuleResponse(
        id = id!!,
        moduleId = module.id!!,
        moduleKey = module.key,
        moduleName = module.name,
        pricePerStudent = pricePerStudent,
        createdAt = createdAt
    )
}