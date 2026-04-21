package com.logiquel.schoolerp.repo

import com.logiquel.schoolerp.entities.RoleEntity
import com.logiquel.schoolerp.entities.RoleStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RoleRepository : JpaRepository<RoleEntity, UUID> {

    fun findByKey(key: String): RoleEntity?

    fun findAllByStatus(status: RoleStatus): List<RoleEntity>

    fun existsByKey(key: String): Boolean
}