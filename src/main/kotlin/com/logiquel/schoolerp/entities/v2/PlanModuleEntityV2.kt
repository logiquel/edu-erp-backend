package com.logiquel.schoolerp.entities.v2

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "plan_modules_v2",
    uniqueConstraints = [
        UniqueConstraint(
            name = "plan_modules_v2_plan_id_module_id_unique",
            columnNames = ["plan_id", "module_id"]
        )
    ]
)
class PlanModuleEntityV2(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    var plan: PlanEntityV2,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    var module: ModuleEntityV2,

    @Column(name = "price_per_student", nullable = false, precision = 10, scale = 2)
    var pricePerStudent: BigDecimal,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)