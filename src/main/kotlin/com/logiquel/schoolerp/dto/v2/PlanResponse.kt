package com.logiquel.schoolerp.dto.v2


import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class PlanResponse(
    val id: UUID,
    val tenantId: UUID,
    val tenantName: String,
    val name: String,
    val modules: List<PlanModuleResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class PlanModuleResponse(
    val id: UUID,
    val moduleId: UUID,
    val moduleKey: String,
    val moduleName: String,
    val pricePerStudent: BigDecimal,
    val createdAt: LocalDateTime
)

data class TenantBillingResponse(
    val id: UUID,
    val tenantId: UUID,
    val tenantName: String,
    val planId: UUID?,
    val planName: String?,
    val billingCycle: String,
    val activatedAt: LocalDateTime?,
    val nextBillingDate: LocalDate?,
    val lastBilledAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)