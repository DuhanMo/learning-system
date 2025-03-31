package io.duhan.security.application.port

import io.duhan.security.domain.TokenClaims

interface JwtProvider {
    fun createAccessToken(
        id: Long,
        roles: List<String>,
    ): String

    fun createRefreshToken(
        id: Long,
        roles: List<String>,
    ): String

    fun validateAndExtractClaims(token: String): TokenClaims
}
