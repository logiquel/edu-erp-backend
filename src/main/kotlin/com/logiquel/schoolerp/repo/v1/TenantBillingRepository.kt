package com.logiquel.schoolerp.repo.v1

import com.logiquel.schoolerp.entities.v1.TenantBillingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.Optional
import java.util.UUID

interface TenantBillingRepository : JpaRepository<TenantBillingEntity, UUID> {

    fun existsByTenantId(tenantId: UUID): Boolean

    fun findByTenantId(tenantId: UUID): Optional<TenantBillingEntity>

    // find all tenants whose billing is due on or before a given date
    // used by billing job
    @Query(
        "SELECT tb FROM TenantBillingEntity tb " +
                "LEFT JOIN FETCH tb.tenant " +
                "LEFT JOIN FETCH tb.plan " +
                "WHERE tb.nextBillingDate <= :date"
    )
    fun findAllDueForBilling(date: LocalDate): List<TenantBillingEntity>
}