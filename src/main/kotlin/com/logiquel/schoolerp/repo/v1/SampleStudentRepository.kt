package com.logiquel.schoolerp.repo.v1

import com.logiquel.schoolerp.entities.v1.SampleStudentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SampleStudentRepository : JpaRepository<SampleStudentEntity, UUID> {

}