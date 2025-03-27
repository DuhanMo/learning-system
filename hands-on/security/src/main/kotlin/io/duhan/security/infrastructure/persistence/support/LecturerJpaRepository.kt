package io.duhan.security.infrastructure.persistence.support

import io.duhan.security.domain.Lecturer
import org.springframework.data.jpa.repository.JpaRepository

interface LecturerJpaRepository : JpaRepository<Lecturer, Long> {
    fun findByEmail(email: String): Lecturer?
}
