package com.logiquel.schoolerp.repo

import com.logiquel.schoolerp.entities.ModuleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ModuleRepository : JpaRepository<ModuleEntity, UUID> {

    fun findByKey(key: String): ModuleEntity?

    fun findAllByIsActiveOrderBySortOrderAsc(isActive: Boolean): List<ModuleEntity>

    fun findAllByRoleKeyOrderBySortOrderAsc(roleKey: String): List<ModuleEntity>

    fun existsByKey(key: String): Boolean

    @Query("SELECT m FROM ModuleEntity m JOIN FETCH m.role")
    fun findAllWithRoles(): List<ModuleEntity>
}