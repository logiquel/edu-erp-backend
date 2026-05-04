package com.logiquel.schoolerp.service.v2

import com.logiquel.schoolerp.dto.common.ApiError
import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v2.TenantBillingResponse
import com.logiquel.schoolerp.entities.v1.BillingCycle
import com.logiquel.schoolerp.entities.v1.TenantBillingEntity
import com.logiquel.schoolerp.repo.PlanRepository
import com.logiquel.schoolerp.repo.TenantBillingRepository
import com.logiquel.schoolerp.repo.TenantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

data class CreateTenantBillingRequest(
    val tenantId: UUID,
    val planId: UUID? = null,
    val billingCycle: String
)

data class UpdateTenantBillingRequest(
    val planId: UUID? = null,
    val billingCycle: String? = null,
    val activatedAt: LocalDateTime? = null
)

@Service
class TenantBillingV2Service(
    private val tenantBillingRepository: TenantBillingRepository,
    private val tenantRepository: TenantRepository,
    private val planRepository: PlanRepository
) {

    // ─────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────
    fun findAll(): ApiResponse<List<TenantBillingResponse>> {
        val billings = tenantBillingRepository.findAll().map { it.toResponse() }
        return ApiResponse(
            success = true,
            status = 200,
            message = "Tenant billings fetched successfully",
            data = billings
        )
    }

    // ─────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────
    fun findById(id: UUID): ApiResponse<TenantBillingResponse> {
        val billing = tenantBillingRepository.findById(id).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "Billing record not found",
                error = ApiError(
                    code = "BILLING_NOT_FOUND",
                    details = listOf("No billing record exists with id: $id")
                )
            )
        return ApiResponse(
            success = true,
            status = 200,
            message = "Billing record fetched successfully",
            data = billing.toResponse()
        )
    }

    // ─────────────────────────────────────
    // GET BY TENANT ID
    // ─────────────────────────────────────
    fun findByTenantId(tenantId: UUID): ApiResponse<TenantBillingResponse> {
        val billing = tenantBillingRepository.findByTenantId(tenantId).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "No billing record found for this tenant",
                error = ApiError(
                    code = "BILLING_NOT_FOUND",
                    details = listOf("No billing record exists for tenant id: $tenantId")
                )
            )
        return ApiResponse(
            success = true,
            status = 200,
            message = "Billing record fetched successfully",
            data = billing.toResponse()
        )
    }

    // ─────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────
    @Transactional
    fun create(request: CreateTenantBillingRequest): ApiResponse<TenantBillingResponse> {
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

        if (tenantBillingRepository.existsByTenantId(request.tenantId)) {
            return ApiResponse(
                success = false,
                status = 409,
                message = "Billing record already exists for this tenant",
                error = ApiError(
                    code = "BILLING_ALREADY_EXISTS",
                    details = listOf("Tenant ${request.tenantId} already has a billing record")
                )
            )
        }

        val cycle = try {
            BillingCycle.valueOf(request.billingCycle)
        } catch (e: IllegalArgumentException) {
            return ApiResponse(
                success = false,
                status = 400,
                message = "Invalid billing cycle",
                error = ApiError(
                    code = "INVALID_BILLING_CYCLE",
                    details = listOf("billingCycle must be one of: monthly, quarterly, annually")
                )
            )
        }

        val plan = request.planId?.let {
            planRepository.findById(it).orElse(null)
                ?: return ApiResponse(
                    success = false,
                    status = 404,
                    message = "Plan not found",
                    error = ApiError(
                        code = "PLAN_NOT_FOUND",
                        details = listOf("No plan exists with id: ${request.planId}")
                    )
                )
        }

        val billing = TenantBillingEntity(
            tenant = tenant,
            plan = plan,
            billingCycle = cycle
        )

        val saved = tenantBillingRepository.save(billing)
        return ApiResponse(
            success = true,
            status = 200,
            message = "Billing record created successfully",
            data = saved.toResponse()
        )
    }

    // ─────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────
    @Transactional
    fun update(id: UUID, request: UpdateTenantBillingRequest): ApiResponse<TenantBillingResponse> {
        val billing = tenantBillingRepository.findById(id).orElse(null)
            ?: return ApiResponse(
                success = false,
                status = 404,
                message = "Billing record not found",
                error = ApiError(
                    code = "BILLING_NOT_FOUND",
                    details = listOf("No billing record exists with id: $id")
                )
            )

        request.billingCycle?.let {
            val cycle = try {
                BillingCycle.valueOf(it)
            } catch (e: IllegalArgumentException) {
                return ApiResponse(
                    success = false,
                    status = 400,
                    message = "Invalid billing cycle",
                    error = ApiError(
                        code = "INVALID_BILLING_CYCLE",
                        details = listOf("billingCycle must be one of: monthly, quarterly, annually")
                    )
                )
            }
            billing.billingCycle = cycle
        }

        request.planId?.let {
            val plan = planRepository.findById(it).orElse(null)
                ?: return ApiResponse(
                    success = false,
                    status = 404,
                    message = "Plan not found",
                    error = ApiError(
                        code = "PLAN_NOT_FOUND",
                        details = listOf("No plan exists with id: $it")
                    )
                )
            billing.plan = plan
        }

        request.activatedAt?.let {
            billing.activatedAt = it
            billing.nextBillingDate = when (billing.billingCycle) {
                BillingCycle.monthly -> it.toLocalDate().plusMonths(1)
                BillingCycle.quarterly -> it.toLocalDate().plusMonths(3)
                BillingCycle.annually -> it.toLocalDate().plusYears(1)
            }
        }

        billing.updatedAt = LocalDateTime.now()
        val updated = tenantBillingRepository.save(billing)

        return ApiResponse(
            success = true,
            status = 200,
            message = "Billing record updated successfully",
            data = updated.toResponse()
        )
    }

    // ─────────────────────────────────────
    // Mapper
    // ─────────────────────────────────────
    private fun TenantBillingEntity.toResponse() = TenantBillingResponse(
        id = id!!,
        tenantId = tenant.id!!,
        tenantName = tenant.name,
        planId = plan?.id,
        planName = plan?.name,
        billingCycle = billingCycle.name,
        activatedAt = activatedAt,
        nextBillingDate = nextBillingDate,
        lastBilledAt = lastBilledAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}