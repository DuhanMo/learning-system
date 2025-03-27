package io.duhan.security.application.port

import io.duhan.security.domain.Admin

interface AdminRepository {
    fun findByEmail(email: String): Admin?
}
