package com.logiquel.schoolerp.entities

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

enum class TenantStatus {
    onboarding, active, suspended, blocked, expired, archived
}

enum class TenantType {
    school, college, coaching, university
}

enum class BoardType {
    CBSE, ICSE, IB, State, Other
}

@Entity
@Table(name = "tenantsv2")
class TenantEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "slug", nullable = false, unique = true, length = 50)
    val slug: String,

    @Column(name = "name", nullable = false, length = 200)
    var name: String,

    @Column(name = "display_name", length = 200)
    var displayName: String? = null,

    @Column(name = "logo_url", columnDefinition = "TEXT")
    var logoUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: TenantStatus = TenantStatus.onboarding,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    var type: TenantType,

    @Column(name = "primary_email", nullable = false, length = 200)
    var primaryEmail: String,

    @Column(name = "primary_phone", length = 20)
    var primaryPhone: String? = null,

    @Column(name = "website", columnDefinition = "TEXT")
    var website: String? = null,

    @Column(name = "address_line1", columnDefinition = "TEXT")
    var addressLine1: String? = null,

    @Column(name = "address_line2", columnDefinition = "TEXT")
    var addressLine2: String? = null,

    @Column(name = "city", length = 100)
    var city: String? = null,

    @Column(name = "state", length = 100)
    var state: String? = null,

    @Column(name = "pincode", length = 10)
    var pincode: String? = null,

    @Column(name = "country", nullable = false, length = 100)
    var country: String = "India",

    @Enumerated(EnumType.STRING)
    @Column(name = "board_type", length = 20)
    var boardType: BoardType? = null,

    @Column(name = "established_year")
    var establishedYear: Int? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)