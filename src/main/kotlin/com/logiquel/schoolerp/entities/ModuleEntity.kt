package com.logiquel.schoolerp.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "modulesv2")
class ModuleEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "key", nullable = false, unique = true, length = 100)
    val key: String,

    @Column(name = "name", nullable = false, length = 200)
    var name: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    var role: RoleEntity,

    @Column(name = "icon", length = 100)
    var icon: String? = null,

    @Column(name = "price_per_student", nullable = false, precision = 10, scale = 2)
    var pricePerStudent: BigDecimal = BigDecimal.ZERO,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = false,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)