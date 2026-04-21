package com.logiquel.schoolerp.dto.v1

import com.logiquel.schoolerp.entities.BoardType
import com.logiquel.schoolerp.entities.TenantStatus
import com.logiquel.schoolerp.entities.TenantType
import java.time.LocalDateTime
import java.util.UUID

// ─────────────────────────────────────
// CREATE REQUEST
// ─────────────────────────────────────
data class CreateTenantRequest(
    val slug: String,
    val name: String,
    val displayName: String? = null,
    val logoUrl: String? = null,
    val status: TenantStatus = TenantStatus.onboarding,
    val type: TenantType,

    // Contact
    val primaryEmail: String,
    val primaryPhone: String? = null,
    val website: String? = null,

    // Location
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val country: String = "India",

    // School Info
    val boardType: BoardType? = null,
    val establishedYear: Int? = null
)

// ─────────────────────────────────────
// UPDATE REQUEST
// all fields optional
// ─────────────────────────────────────
data class UpdateTenantRequest(
    val name: String? = null,
    val displayName: String? = null,
    val logoUrl: String? = null,
    val status: TenantStatus? = null,
    val type: TenantType? = null,

    // Contact
    val primaryEmail: String? = null,
    val primaryPhone: String? = null,
    val website: String? = null,

    // Location
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val country: String? = null,

    // School Info
    val boardType: BoardType? = null,
    val establishedYear: Int? = null
)

// ─────────────────────────────────────
// RESPONSE
// ─────────────────────────────────────
data class TenantResponse(
    val id: UUID,
    val slug: String,
    val name: String,
    val displayName: String?,
    val logoUrl: String?,
    val status: TenantStatus,
    val type: TenantType,

    // Contact
    val primaryEmail: String,
    val primaryPhone: String?,
    val website: String?,

    // Location
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val state: String?,
    val pincode: String?,
    val country: String,

    // School Info
    val boardType: BoardType?,
    val establishedYear: Int?,

    // Timestamps
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)