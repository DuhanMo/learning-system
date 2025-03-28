package io.duhan.security.application.dto

data class AccessToken(
    val accessToken: String,
    val refreshToken: String,
)
