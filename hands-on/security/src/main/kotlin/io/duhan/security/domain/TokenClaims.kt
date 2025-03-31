package io.duhan.security.domain

import java.time.Instant

data class TokenClaims(
    val id: Long,
    val roles: List<String>,
    val permissions: List<String> = emptyList(),
    val isValid: Boolean,
    val tokenType: String? = null,
    val expirationTime: Instant? = null,
)
