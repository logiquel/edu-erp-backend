package com.logiquel.schoolerp.repo.v2

import com.logiquel.schoolerp.entities.v2.ModuleEntityV2
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ModuleRepositoryV2 : JpaRepository<ModuleEntityV2, UUID> {

    fun findByKey(key: String): ModuleEntityV2?

    fun findAllByIsActiveOrderBySortOrderAsc(isActive: Boolean): List<ModuleEntityV2>

    fun findAllByRoleKeyOrderBySortOrderAsc(roleKey: String): List<ModuleEntityV2>

    fun existsByKey(key: String): Boolean

    @Query("SELECT m FROM ModuleEntity m JOIN FETCH m.role")
    fun findAllWithRoles(): List<ModuleEntityV2>
}