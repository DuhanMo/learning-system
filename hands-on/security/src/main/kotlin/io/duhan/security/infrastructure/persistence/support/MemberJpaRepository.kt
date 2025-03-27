package io.duhan.security.infrastructure.persistence.support

import io.duhan.security.domain.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberJpaRepository : JpaRepository<Member, Long> {
    fun findByEmail(email: String): Member?
}
