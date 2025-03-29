package io.duhan.security.infrastructure.persistence.support

import io.duhan.security.domain.AdminRole
import org.springframework.data.jpa.repository.JpaRepository

interface AdminRoleJpaRepository : JpaRepository<AdminRole, Long> {
    fun findAllByAdminId(adminId: Long): List<AdminRole>
}
