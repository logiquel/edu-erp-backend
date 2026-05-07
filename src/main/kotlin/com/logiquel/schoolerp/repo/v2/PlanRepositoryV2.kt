package com.logiquel.schoolerp.repo.v2

import com.logiquel.schoolerp.entities.v2.PlanEntityV2
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional
import java.util.UUID

interface PlanRepositoryV2 : JpaRepository<PlanEntityV2, UUID> {

    fun existsByTenantId(tenantId: UUID): Boolean

    fun findByTenantId(tenantId: UUID): Optional<PlanEntityV2>

    @Query("SELECT p FROM PlanEntity p LEFT JOIN FETCH p.tenant WHERE p.id = :id")
    fun findByIdWithTenant(id: UUID): Optional<PlanEntityV2>
}