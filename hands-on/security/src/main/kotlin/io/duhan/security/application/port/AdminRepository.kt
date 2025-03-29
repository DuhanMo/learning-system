package io.duhan.security.application.port

import io.duhan.security.domain.Admin
import io.duhan.security.domain.AdminRole

interface AdminRepository {
    fun findByEmail(email: String): Admin?

    fun findRolesById(adminId: Long): List<AdminRole>
}
