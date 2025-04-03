package io.duhan.security.infrastructure.security.provider

import io.duhan.security.application.port.JwtProvider
import io.duhan.security.infrastructure.persistence.support.MemberJpaRepository
import io.duhan.security.infrastructure.security.token.EmailAuthenticationToken.MemberEmailAuthenticationToken
import io.duhan.security.infrastructure.security.token.RefreshTokenAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class RefreshTokenAuthenticationProvider(
    private val jwtProvider: JwtProvider,
    private val memberRepository: MemberJpaRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        val token = authentication as RefreshTokenAuthenticationToken
        val claims = jwtProvider.validateAndExtractClaims(token.principal as String)

        claims.tokenType

        val email = authentication.principal as String
        val password = authentication.credentials as String
        val member =
            memberRepository.findByEmail(email)
                ?: throw BadCredentialsException("Invalid email or password")

        if (!passwordEncoder.matches(password, member.password)) {
            throw BadCredentialsException("Invalid email or password")
        }

        return RefreshTokenAuthenticationToken(
            token =,
            authorities =,
            details =,
            userType =,
        )
//        return MemberEmailAuthenticationToken(
//            email = email,
//            authorities = listOf(SimpleGrantedAuthority(MEMBER.roleName()), SimpleGrantedAuthority(member.grade)),
//            details = UserDetails(member.id),
//        )
    }

    override fun supports(authentication: Class<*>): Boolean =
        MemberEmailAuthenticationToken::class.java.isAssignableFrom(authentication)
}
