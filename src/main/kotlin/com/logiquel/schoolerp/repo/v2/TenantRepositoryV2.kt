package com.logiquel.schoolerp.repo.v2

import com.logiquel.schoolerp.entities.v2.TenantEntityV2
import com.logiquel.schoolerp.entities.v2.TenantStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TenantRepositoryV2 : JpaRepository<TenantEntityV2, UUID> {

    fun findBySlug(slug: String): TenantEntityV2?

    fun findAllByStatus(status: TenantStatus): List<TenantEntityV2>

    fun existsBySlug(slug: String): Boolean

    fun existsByPrimaryEmail(email: String): Boolean
}