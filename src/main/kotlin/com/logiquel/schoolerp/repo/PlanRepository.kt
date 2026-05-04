package com.logiquel.schoolerp.repo

import com.logiquel.schoolerp.entities.v1.PlanEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional
import java.util.UUID

interface PlanRepository : JpaRepository<PlanEntity, UUID> {

    fun existsByTenantId(tenantId: UUID): Boolean

    fun findByTenantId(tenantId: UUID): Optional<PlanEntity>

    @Query("SELECT p FROM PlanEntity p LEFT JOIN FETCH p.tenant WHERE p.id = :id")
    fun findByIdWithTenant(id: UUID): Optional<PlanEntity>
}