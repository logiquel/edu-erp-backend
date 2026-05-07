package com.logiquel.schoolerp.service.v2


import com.logiquel.schoolerp.dto.common.ApiError
import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v2.CreateModuleRequestV2
import com.logiquel.schoolerp.dto.v2.ModuleResponseV2
import com.logiquel.schoolerp.dto.v2.UpdateModuleRequestV2
import com.logiquel.schoolerp.entities.v2.ModuleEntityV2
import com.logiquel.schoolerp.repo.v2.ModuleRepositoryV2
import com.logiquel.schoolerp.repo.RoleRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ModulesV2Service(
    private val moduleRepositoryV2: ModuleRepositoryV2,
    private val roleRepository: RoleRepository
) {

    // ─────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────
    @Cacheable(cacheNames = ["modules"])
    fun findAll(): List<ModuleResponseV2> {
       return  moduleRepositoryV2.findAllWithRoles().map { it.toResponse() }

    }

    // ─────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────
    fun findById(id: UUID): ApiResponse<ModuleResponseV2> {
        val module = moduleRepositoryV2.findById(id).orElse(null)
            ?: return ApiResponse(
                success = false,
                status  = 404,
                message = "Module not found",
                error   = ApiError(
                    code = "MODULE_NOT_FOUND",
                    details = listOf("No module exists with id: $id")
                )
            )

        return ApiResponse(
            success = true,
            status  = 200,
            message = "Module fetched successfully",
            data    = module.toResponse()
        )
    }

    // ─────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────
    fun create(request: CreateModuleRequestV2): ApiResponse<ModuleResponseV2> {
        val role = roleRepository.findById(request.roleId).orElse(null)
            ?: return ApiResponse(
                success = false,
                status  = 404,
                message = "Role not found",
                error   = ApiError(
                    code    = "ROLE_NOT_FOUND",
                    details = listOf("No role exists with id: ${request.roleId}")
                )
            )

        val module = ModuleEntityV2(
            key             = request.key,
            name            = request.name,
            description     = request.description,
            role            = role,
            icon            = request.icon,
            pricePerStudent = request.pricePerStudent,
            isActive        = request.isActive,
            sortOrder       = request.sortOrder
        )

        val saved = moduleRepositoryV2.save(module)

        return ApiResponse(
            success = true,
            status  = 200,
            message = "Module created successfully",
            data    = saved.toResponse()
        )
    }

    // ─────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────
    @CacheEvict(cacheNames = ["modules"],allEntries = true)
    fun update(id: UUID, request: UpdateModuleRequestV2): ApiResponse<ModuleResponseV2> {
        val module = moduleRepositoryV2.findById(id).orElse(null)
            ?: return ApiResponse(
                success = false,
                status  = 404,
                message = "Module not found",
                error   = ApiError(
                    code    = "MODULE_NOT_FOUND",
                    details = listOf("No module exists with id: $id")
                )
            )

        request.name?.let            { module.name = it }
        request.description?.let     { module.description = it }
        request.icon?.let            { module.icon = it }
        request.pricePerStudent?.let { module.pricePerStudent = it }
        request.isActive?.let        { module.isActive = it }
        request.sortOrder?.let       { module.sortOrder = it }
        request.roleId?.let { roleId ->
            val role = roleRepository.findById(roleId).orElse(null)
                ?: return ApiResponse(
                    success = false,
                    status  = 404,
                    message = "Role not found",
                    error   = ApiError(
                        code    = "ROLE_NOT_FOUND",
                        details = listOf("No role exists with id: $roleId")
                    )
                )
            module.role = role
        }

        module.updatedAt = LocalDateTime.now()
        val updated = moduleRepositoryV2.save(module)

        return ApiResponse(
            success = true,
            status  = 200,
            message = "Module updated successfully",
            data    = updated.toResponse()
        )
    }

    // ─────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────
    @CacheEvict(cacheNames = ["modules"],allEntries = true)
    fun delete(id: UUID): ApiResponse<Nothing> {
        if (!moduleRepositoryV2.existsById(id)) {
            return ApiResponse(
                success = false,
                status  = 404,
                message = "Module not found",
                error   = ApiError(
                    code    = "MODULE_NOT_FOUND",
                    details = listOf("No module exists with id: $id")
                )
            )
        }

        moduleRepositoryV2.deleteById(id)

        return ApiResponse(
            success = true,
            status  = 200,
            message = "Module deleted successfully"
        )
    }

    // ─────────────────────────────────────
    // Mapper — Entity to Response
    // ─────────────────────────────────────
    private fun ModuleEntityV2.toResponse() = ModuleResponseV2(
        id              = id!!,
        key             = key,
        name            = name,
        description     = description,
        roleId          = role.id!!,
        roleKey         = role.key,
        icon            = icon,
        pricePerStudent = pricePerStudent,
        isActive        = isActive,
        sortOrder       = sortOrder,
        createdAt       = createdAt,
        updatedAt       = updatedAt
    )
}