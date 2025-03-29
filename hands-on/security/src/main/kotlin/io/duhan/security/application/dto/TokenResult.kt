package io.duhan.security.application.dto

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
)
