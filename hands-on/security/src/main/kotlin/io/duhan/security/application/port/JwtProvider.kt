package io.duhan.security.application.port

interface JwtProvider {
    fun createAccessToken(): String

    fun createRefreshToken(): String
}
