package com.logiquel.schoolerp.repo.v2

import com.logiquel.schoolerp.entities.v2.PlanModuleEntityV2
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface PlanModuleRepositoryV2 : JpaRepository<PlanModuleEntityV2, UUID> {

    fun findAllByPlanId(planId: UUID): List<PlanModuleEntityV2>

    fun existsByPlanIdAndModuleId(planId: UUID, moduleId: UUID): Boolean

    fun deleteByPlanIdAndModuleId(planId: UUID, moduleId: UUID)

    @Query(
        "SELECT pm FROM PlanModuleEntity pm " +
                "LEFT JOIN FETCH pm.module " +
                "WHERE pm.plan.id = :planId"
    )
    fun findAllByPlanIdWithModule(planId: UUID): List<PlanModuleEntityV2>
}