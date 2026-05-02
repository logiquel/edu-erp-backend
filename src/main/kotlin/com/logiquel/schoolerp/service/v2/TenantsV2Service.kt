package com.logiquel.schoolerp.service.v2

import com.logiquel.schoolerp.dto.common.ApiError
import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v1.CreateTenantRequest
import com.logiquel.schoolerp.dto.v1.TenantResponse
import com.logiquel.schoolerp.dto.v1.UpdateTenantRequest
import com.logiquel.schoolerp.entities.TenantEntity
import com.logiquel.schoolerp.repo.TenantRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class TenantsV2Service(
    private val tenantRepository: TenantRepository
) {

    // ─────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────
    fun findAll(): ApiResponse<List<TenantResponse>> {
        val tenants = tenantRepository.findAll().map { it.toResponse() }
        return ApiResponse(
            success = true,
            status  = 200,
            message = "Tenants fetched successfully",
            data    = tenants
        )
    }

    // ─────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────
    fun findById(id: UUID): ApiResponse<TenantResponse> {
        val tenant = tenantRepository.findById(id).orElse(null)
            ?: return ApiResponse(
                success = false,
                status  = 404,
                message = "Tenant not found",
                error   = ApiError(
                    code    = "TENANT_NOT_FOUND",
                    details = listOf("No tenant exists with id: $id")
                )
            )
        return ApiResponse(
            success = true,
            status  = 200,
            message = "Tenant fetched successfully",
            data    = tenant.toResponse()
        )
    }

    // ─────────────────────────────────────
    // GET BY SLUG
    // ─────────────────────────────────────
    fun findBySlug(slug: String): ApiResponse<TenantResponse> {
        val tenant = tenantRepository.findBySlug(slug)
            ?: return ApiResponse(
                success = false,
                status  = 404,
                message = "Tenant not found",
                error   = ApiError(
                    code    = "TENANT_NOT_FOUND",
                    details = listOf("No tenant exists with slug: $slug")
                )
            )
        return ApiResponse(
            success = true,
            status  = 200,
            message = "Tenant fetched successfully",
            data    = tenant.toResponse()
        )
    }

    // ─────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────
    fun create(request: CreateTenantRequest): ApiResponse<TenantResponse> {
        // check slug uniqueness
        if (tenantRepository.existsBySlug(request.slug)) {
            return ApiResponse(
                success = false,
                status  = 409,
                message = "Slug already exists",
                error   = ApiError(
                    code    = "SLUG_ALREADY_EXISTS",
                    details = listOf("A tenant with slug '${request.slug}' already exists")
                )
            )
        }

        // check email uniqueness
        if (tenantRepository.existsByPrimaryEmail(request.primaryEmail)) {
            return ApiResponse(
                success = false,
                status  = 409,
                message = "Email already exists",
                error   = ApiError(
                    code    = "EMAIL_ALREADY_EXISTS",
                    details = listOf("A tenant with email '${request.primaryEmail}' already exists")
                )
            )
        }

        val tenant = TenantEntity(
            slug            = request.slug,
            name            = request.name,
            displayName     = request.displayName,
            logoUrl         = request.logoUrl,
            status          = request.status,
            type            = request.type,
            primaryEmail    = request.primaryEmail,
            primaryPhone    = request.primaryPhone,
            website         = request.website,
            addressLine1    = request.addressLine1,
            addressLine2    = request.addressLine2,
            city            = request.city,
            state           = request.state,
            pincode         = request.pincode,
            country         = request.country,
            boardType       = request.boardType,
            establishedYear = request.establishedYear
        )

        val saved = tenantRepository.save(tenant)
        return ApiResponse(
            success = true,
            status  = 200,
            message = "Tenant created successfully",
            data    = saved.toResponse()
        )
    }

    // ─────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────
    fun update(id: UUID, request: UpdateTenantRequest): ApiResponse<TenantResponse> {
        val tenant = tenantRepository.findById(id).orElse(null)
            ?: return ApiResponse(
                success = false,
                status  = 404,
                message = "Tenant not found",
                error   = ApiError(
                    code    = "TENANT_NOT_FOUND",
                    details = listOf("No tenant exists with id: $id")
                )
            )

        request.name?.let            { tenant.name = it }
        request.displayName?.let     { tenant.displayName = it }
        request.logoUrl?.let         { tenant.logoUrl = it }
        request.status?.let          { tenant.status = it }
        request.type?.let            { tenant.type = it }
        request.primaryEmail?.let    { tenant.primaryEmail = it }
        request.primaryPhone?.let    { tenant.primaryPhone = it }
        request.website?.let         { tenant.website = it }
        request.addressLine1?.let    { tenant.addressLine1 = it }
        request.addressLine2?.let    { tenant.addressLine2 = it }
        request.city?.let            { tenant.city = it }
        request.state?.let           { tenant.state = it }
        request.pincode?.let         { tenant.pincode = it }
        request.country?.let         { tenant.country = it }
        request.boardType?.let       { tenant.boardType = it }
        request.establishedYear?.let { tenant.establishedYear = it }

        tenant.updatedAt = LocalDateTime.now()

        val updated = tenantRepository.save(tenant)
        return ApiResponse(
            success = true,
            status  = 200,
            message = "Tenant updated successfully",
            data    = updated.toResponse()
        )
    }

    // ─────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────
    fun delete(id: UUID): ApiResponse<Nothing> {
        if (!tenantRepository.existsById(id)) {
            return ApiResponse(
                success = false,
                status  = 404,
                message = "Tenant not found",
                error   = ApiError(
                    code    = "TENANT_NOT_FOUND",
                    details = listOf("No tenant exists with id: $id")
                )
            )
        }
        tenantRepository.deleteById(id)
        return ApiResponse(
            success = true,
            status  = 200,
            message = "Tenant deleted successfully"
        )
    }

    // ─────────────────────────────────────
    // Mapper — Entity to Response
    // ─────────────────────────────────────
    private fun TenantEntity.toResponse() = TenantResponse(
        id              = id!!,
        slug            = slug,
        name            = name,
        displayName     = displayName,
        logoUrl         = logoUrl,
        status          = status,
        type            = type,
        primaryEmail    = primaryEmail,
        primaryPhone    = primaryPhone,
        website         = website,
        addressLine1    = addressLine1,
        addressLine2    = addressLine2,
        city            = city,
        state           = state,
        pincode         = pincode,
        country         = country,
        boardType       = boardType,
        establishedYear = establishedYear,
        createdAt       = createdAt,
        updatedAt       = updatedAt
    )
}