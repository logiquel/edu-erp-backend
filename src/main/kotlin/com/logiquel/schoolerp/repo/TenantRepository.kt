package com.logiquel.schoolerp.repo

import com.logiquel.schoolerp.entities.TenantEntity
import com.logiquel.schoolerp.entities.TenantStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TenantRepository : JpaRepository<TenantEntity, UUID> {

    fun findBySlug(slug: String): TenantEntity?

    fun findAllByStatus(status: TenantStatus): List<TenantEntity>

    fun existsBySlug(slug: String): Boolean

    fun existsByPrimaryEmail(email: String): Boolean
}