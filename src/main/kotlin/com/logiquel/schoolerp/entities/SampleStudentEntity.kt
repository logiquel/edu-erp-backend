package com.logiquel.schoolerp.entities

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "sample_students")
data class SampleStudentEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false, unique = true)
    var rollNumber: String,

    @Column(nullable = false)
    var className: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
)