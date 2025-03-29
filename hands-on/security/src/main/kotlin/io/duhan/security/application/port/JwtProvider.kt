package io.duhan.security.application.port

interface JwtProvider {
    fun createAccessToken(
        id: Long,
        roles: List<String>,
    ): String

    fun createRefreshToken(
        id: Long,
        roles: List<String>,
    ): String
}
