package com.logiquel.schoolerp.entities

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

enum class RoleStatus {
    ACTIVE, INACTIVE
}

@Entity
@Table(name = "roles")
class RoleEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "key", nullable = false, unique = true, length = 50)
    val key: String,

    @Column(name = "value", nullable = false, unique = true, length = 50)
    val value: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "role_status")
    val status: RoleStatus = RoleStatus.ACTIVE,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)