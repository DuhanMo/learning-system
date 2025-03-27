package io.duhan.security.infrastructure.security.adapter

import io.duhan.security.application.dto.AuthCommand
import io.duhan.security.application.dto.AuthResult
import io.duhan.security.application.dto.AuthResult.AdminAuthResult
import io.duhan.security.application.dto.AuthResult.LecturerAuthResult
import io.duhan.security.application.dto.AuthResult.MemberAuthResult
import io.duhan.security.application.port.Authenticator
import io.duhan.security.infrastructure.security.token.AdminDetails
import io.duhan.security.infrastructure.security.token.AuthenticationTokenFactory
import io.duhan.security.infrastructure.security.token.EmailAuthenticationToken.AdminEmailAuthenticationToken
import io.duhan.security.infrastructure.security.token.EmailAuthenticationToken.LecturerEmailAuthenticationToken
import io.duhan.security.infrastructure.security.token.EmailAuthenticationToken.MemberEmailAuthenticationToken
import io.duhan.security.infrastructure.security.token.JwtAuthenticationToken
import io.duhan.security.infrastructure.security.token.LecturerDetails
import io.duhan.security.infrastructure.security.token.MemberDetails
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.stereotype.Component

@Component
class SpringSecurityAuthenticator(
    private val authenticationManager: AuthenticationManager,
    private val authenticationTokenFactory: AuthenticationTokenFactory,
) : Authenticator {
    override fun authenticate(authCommand: AuthCommand): AuthResult {
        val authentication = authenticationManager.authenticate(authenticationTokenFactory.createToken(authCommand))
        return when (authentication) {
            is AdminEmailAuthenticationToken -> createAminAuthResult(authentication)
            is LecturerEmailAuthenticationToken -> createLecturerAuthResult(authentication)
            is MemberEmailAuthenticationToken -> createMemberAuthResult(authentication)
            is JwtAuthenticationToken -> createMemberAuthResult(authentication)
            else -> throw IllegalArgumentException("Unsupported authentication type {}: ${authentication.javaClass.name}")
        }
    }

    private fun createAminAuthResult(authentication: AbstractAuthenticationToken): AdminAuthResult {
        val details = authentication.details as AdminDetails
        return AdminAuthResult(
            id = details.id,
            name = details.name,
            email = details.email,
            department = details.department,
            roles = authentication.authorities.map { it.authority },
        )
    }

    private fun createLecturerAuthResult(authentication: AbstractAuthenticationToken): LecturerAuthResult {
        val details = authentication.details as LecturerDetails
        return LecturerAuthResult(
            id = details.id,
            name = details.name,
            email = details.email,
            subject = details.subject,
            roles = authentication.authorities.map { it.authority },
        )
    }

    private fun createMemberAuthResult(authentication: AbstractAuthenticationToken): MemberAuthResult {
        val details = authentication.details as MemberDetails
        return MemberAuthResult(
            id = details.id,
            name = details.name,
            grade = details.grade,
            roles = authentication.authorities.map { it.authority },
        )
    }
}
