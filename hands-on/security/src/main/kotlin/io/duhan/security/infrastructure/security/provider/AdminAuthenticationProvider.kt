package io.duhan.security.infrastructure.security.provider

import io.duhan.security.domain.UserType.ADMIN
import io.duhan.security.infrastructure.persistence.support.AdminJpaRepository
import io.duhan.security.infrastructure.security.token.EmailAuthenticationToken.AdminEmailAuthenticationToken
import io.duhan.security.infrastructure.security.token.UserDetails
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AdminAuthenticationProvider(
    private val adminJpaRepository: AdminJpaRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val email = authentication.principal as String
        val password = authentication.credentials as String
        val admin = adminJpaRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid email or password")

        if (!passwordEncoder.matches(password, admin.password)) {
            throw BadCredentialsException("Invalid email or password")
        }

        return AdminEmailAuthenticationToken(
            email = email,
            authorities = listOf(SimpleGrantedAuthority(ADMIN.roleName())),
            details = UserDetails(admin.id),
        )
    }

    override fun supports(authentication: Class<*>): Boolean = AdminEmailAuthenticationToken::class.java.isAssignableFrom(authentication)
}
