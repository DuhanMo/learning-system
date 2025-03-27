package io.duhan.security.infrastructure.persistence.adapter

import io.duhan.security.application.port.AdminRepository
import io.duhan.security.domain.Admin
import io.duhan.security.infrastructure.persistence.support.AdminJpaRepository
import org.springframework.stereotype.Repository

@Repository
class AdminRepositoryImpl(private val adminJpaRepository: AdminJpaRepository) : AdminRepository {
    override fun findByEmail(email: String): Admin? = adminJpaRepository.findByEmail(email)
}
