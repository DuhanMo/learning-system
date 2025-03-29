package io.duhan.security.infrastructure.security.provider

import io.duhan.security.application.port.AdminRepository
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
    private val adminRepository: AdminRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val email = authentication.principal as String
        val password = authentication.credentials as String
        val admin = adminRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid email or password")
        val roles = adminRepository.findRolesById(admin.id)
        if (!passwordEncoder.matches(password, admin.password)) {
            throw BadCredentialsException("Invalid email or password")
        }

        return AdminEmailAuthenticationToken(
            email = email,
            authorities = roles.map { SimpleGrantedAuthority(it.role) },
            details = UserDetails(admin.id),
        )
    }

    override fun supports(authentication: Class<*>): Boolean = AdminEmailAuthenticationToken::class.java.isAssignableFrom(authentication)
}
