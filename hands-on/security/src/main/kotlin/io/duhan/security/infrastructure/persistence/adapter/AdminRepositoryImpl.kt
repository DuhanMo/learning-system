package io.duhan.security.infrastructure.persistence.adapter

import io.duhan.security.application.port.AdminRepository
import io.duhan.security.domain.Admin
import io.duhan.security.domain.AdminRole
import io.duhan.security.infrastructure.persistence.support.AdminJpaRepository
import io.duhan.security.infrastructure.persistence.support.AdminRoleJpaRepository
import org.springframework.stereotype.Repository

@Repository
class AdminRepositoryImpl(
    private val adminJpaRepository: AdminJpaRepository,
    private val adminRoleJpaRepository: AdminRoleJpaRepository,
) : AdminRepository {
    override fun findByEmail(email: String): Admin? = adminJpaRepository.findByEmail(email)

    override fun findRolesById(adminId: Long): List<AdminRole> = adminRoleJpaRepository.findAllByAdminId(adminId)
}
