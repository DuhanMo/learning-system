package io.duhan.security.infrastructure.persistence.support

import io.duhan.security.domain.Admin
import org.springframework.data.jpa.repository.JpaRepository

interface AdminJpaRepository : JpaRepository<Admin, Long> {
    fun findByEmail(email: String): Admin?
}
