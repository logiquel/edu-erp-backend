package com.logiquel.schoolerp.repo

import com.logiquel.schoolerp.entities.SampleStudentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SampleStudentRepository : JpaRepository<SampleStudentEntity, UUID> {

}