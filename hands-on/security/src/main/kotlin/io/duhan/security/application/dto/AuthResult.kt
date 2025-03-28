package io.duhan.security.application.dto

data class AuthResult(
    val id: Long,
    val roles: List<String>,
)
