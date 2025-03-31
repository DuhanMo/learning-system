package io.duhan.security.infrastructure.security.filter

import io.duhan.security.application.port.JwtProvider
import io.duhan.security.infrastructure.security.token.JwtAuthenticationToken
import io.duhan.security.infrastructure.security.token.UserDetails
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
    ) {
        val token = extractTokenFromRequest(request)
        if (token != null) {
            val claims = jwtProvider.validateAndExtractClaims(token)
            if (claims.isValid) {
                val authorities =
                    claims.roles.map { SimpleGrantedAuthority(it) } + claims.permissions.map { SimpleGrantedAuthority(it) }
                val authentication =
                    JwtAuthenticationToken(
                        token = token,
                        authorities = authorities,
                        details = UserDetails(claims.id),
                    )
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        chain.doFilter(request, response)
    }

    private fun extractTokenFromRequest(request: HttpServletRequest): String? =
        request.getHeader(AUTHORIZATION).takeIf { it != null && it.startsWith(BEARER_PREFIX) }
            ?.substring(BEARER_PREFIX.length)

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
