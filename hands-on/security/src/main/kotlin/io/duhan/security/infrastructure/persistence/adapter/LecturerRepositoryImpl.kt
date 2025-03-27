package io.duhan.security.infrastructure.persistence.adapter

import io.duhan.security.application.port.LecturerRepository
import io.duhan.security.domain.Lecturer
import io.duhan.security.infrastructure.persistence.support.LecturerJpaRepository
import org.springframework.stereotype.Repository

@Repository
class LecturerRepositoryImpl(private val lecturerJpaRepository: LecturerJpaRepository) : LecturerRepository {
    override fun findByEmail(email: String): Lecturer? = lecturerJpaRepository.findByEmail(email)
}
