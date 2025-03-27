package io.duhan.security.application.port

import io.duhan.security.domain.Member

interface MemberRepository {
    fun findByEmail(email: String): Member?
}
