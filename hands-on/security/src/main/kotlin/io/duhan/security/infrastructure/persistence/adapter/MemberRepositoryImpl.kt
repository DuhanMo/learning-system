package io.duhan.security.infrastructure.persistence.adapter

import io.duhan.security.application.port.MemberRepository
import io.duhan.security.domain.Member
import io.duhan.security.infrastructure.persistence.support.MemberJpaRepository
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryImpl(private val memberJpaRepository: MemberJpaRepository) : MemberRepository {
    override fun findByEmail(email: String): Member? = memberJpaRepository.findByEmail(email)
}
