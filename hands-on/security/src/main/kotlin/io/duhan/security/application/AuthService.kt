package io.duhan.security.application

import io.duhan.security.application.dto.AuthCommand
import io.duhan.security.application.dto.TokenResult
import io.duhan.security.application.port.Authenticator
import io.duhan.security.application.port.JwtProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val authenticator: Authenticator,
    private val jwtProvider: JwtProvider,
) {
    @Transactional
    fun authenticate(command: AuthCommand): TokenResult {
        val authResult = authenticator.authenticate(command)
        return TokenResult(
            accessToken = jwtProvider.createAccessToken(authResult.id, authResult.roles),
            refreshToken = jwtProvider.createRefreshToken(authResult.id, authResult.roles),
        )
    }
}
