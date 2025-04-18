package io.duhan.security.infrastructure.security.provider

import io.duhan.security.domain.UserType.LECTURER
import io.duhan.security.infrastructure.persistence.support.LecturerJpaRepository
import io.duhan.security.infrastructure.security.token.EmailAuthenticationToken.LecturerEmailAuthenticationToken
import io.duhan.security.infrastructure.security.token.UserDetails
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class LecturerAuthenticationProvider(
    private val lecturerRepository: LecturerJpaRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val email = authentication.principal as String
        val password = authentication.credentials as String
        val lecturer =
            lecturerRepository.findByEmail(email)
                ?: throw BadCredentialsException("Invalid email or password")

        if (!passwordEncoder.matches(password, lecturer.password)) {
            throw BadCredentialsException("Invalid email or password")
        }

        return LecturerEmailAuthenticationToken(
            email = email,
            authorities = listOf(SimpleGrantedAuthority(LECTURER.roleName())),
            details = UserDetails(lecturer.id),
        )
    }

    override fun supports(authentication: Class<*>): Boolean = LecturerEmailAuthenticationToken::class.java.isAssignableFrom(authentication)
}
