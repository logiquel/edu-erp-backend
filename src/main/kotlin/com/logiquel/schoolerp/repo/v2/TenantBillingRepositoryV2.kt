package com.logiquel.schoolerp.repo.v2

import com.logiquel.schoolerp.entities.v2.TenantBillingEntityV2
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.Optional
import java.util.UUID

interface TenantBillingRepositoryV2 : JpaRepository<TenantBillingEntityV2, UUID> {

    fun existsByTenantId(tenantId: UUID): Boolean

    fun findByTenantId(tenantId: UUID): Optional<TenantBillingEntityV2>

    // find all tenants whose billing is due on or before a given date
    // used by billing job
    @Query(
        "SELECT tb FROM TenantBillingEntity tb " +
                "LEFT JOIN FETCH tb.tenant " +
                "LEFT JOIN FETCH tb.plan " +
                "WHERE tb.nextBillingDate <= :date"
    )
    fun findAllDueForBilling(date: LocalDate): List<TenantBillingEntityV2>
}