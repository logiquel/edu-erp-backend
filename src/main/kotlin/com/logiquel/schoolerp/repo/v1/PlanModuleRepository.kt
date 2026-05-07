package com.logiquel.schoolerp.repo.v1

import com.logiquel.schoolerp.entities.v1.PlanModuleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface PlanModuleRepository : JpaRepository<PlanModuleEntity, UUID> {

    fun findAllByPlanId(planId: UUID): List<PlanModuleEntity>

    fun existsByPlanIdAndModuleId(planId: UUID, moduleId: UUID): Boolean

    fun deleteByPlanIdAndModuleId(planId: UUID, moduleId: UUID)

    @Query(
        "SELECT pm FROM PlanModuleEntity pm " +
                "LEFT JOIN FETCH pm.module " +
                "WHERE pm.plan.id = :planId"
    )
    fun findAllByPlanIdWithModule(planId: UUID): List<PlanModuleEntity>
}