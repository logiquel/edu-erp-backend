package com.logiquel.schoolerp.dto.v1

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

// ─────────────────────────────────────
// CREATE REQUEST
// ─────────────────────────────────────
data class CreateModuleRequest(
    val key: String,
    val name: String,
    val description: String? = null,
    val roleId: UUID,
    val icon: String? = null,
    val pricePerStudent: BigDecimal = BigDecimal.ZERO,
    val isActive: Boolean = false,
    val sortOrder: Int = 0
)

// ─────────────────────────────────────
// UPDATE REQUEST
// all fields optional — only send what you want to change
// ─────────────────────────────────────
data class UpdateModuleRequest(
    val name: String? = null,
    val description: String? = null,
    val roleId: UUID? = null,
    val icon: String? = null,
    val pricePerStudent: BigDecimal? = null,
    val isActive: Boolean? = null,
    val sortOrder: Int? = null
)

// ─────────────────────────────────────
// RESPONSE
// ─────────────────────────────────────
data class ModuleResponse(
    val id: UUID,
    val key: String,
    val name: String,
    val description: String?,
    val roleId: UUID,
    val roleKey: String,
    val icon: String?,
    val pricePerStudent: BigDecimal,
    val isActive: Boolean,
    val sortOrder: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)