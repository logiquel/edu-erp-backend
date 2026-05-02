package com.logiquel.schoolerp.service.v2


import com.logiquel.schoolerp.dto.common.ApiError
import com.logiquel.schoolerp.dto.common.ApiResponse
import com.logiquel.schoolerp.dto.v1.CreateModuleRequest
import com.logiquel.schoolerp.dto.v1.ModuleResponse
import com.logiquel.schoolerp.dto.v1.UpdateModuleRequest
import com.logiquel.schoolerp.entities.ModuleEntity
import com.logiquel.schoolerp.repo.ModuleRepository
import com.logiquel.schoolerp.repo.RoleRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ModulesV2Service(
    private val moduleRepository: ModuleRepository,
    private val roleRepository: RoleRepository
) {

    // ─────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────
    @Cacheable(cacheNames = ["modules"])
    fun findAll(): List<ModuleResponse> {
       return  moduleRepository.findAllWithRoles().map { it.toResponse() }

    }

    // ─────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────
    fun findById(id: UUID): ApiResponse<ModuleResponse> {
        val module = moduleRepository.findById(id).orElse(null)
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
    fun create(request: CreateModuleRequest): ApiResponse<ModuleResponse> {
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

        val module = ModuleEntity(
            key             = request.key,
            name            = request.name,
            description     = request.description,
            role            = role,
            icon            = request.icon,
            pricePerStudent = request.pricePerStudent,
            isActive        = request.isActive,
            sortOrder       = request.sortOrder
        )

        val saved = moduleRepository.save(module)

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
    fun update(id: UUID, request: UpdateModuleRequest): ApiResponse<ModuleResponse> {
        val module = moduleRepository.findById(id).orElse(null)
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
        val updated = moduleRepository.save(module)

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
        if (!moduleRepository.existsById(id)) {
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

        moduleRepository.deleteById(id)

        return ApiResponse(
            success = true,
            status  = 200,
            message = "Module deleted successfully"
        )
    }

    // ─────────────────────────────────────
    // Mapper — Entity to Response
    // ─────────────────────────────────────
    private fun ModuleEntity.toResponse() = ModuleResponse(
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