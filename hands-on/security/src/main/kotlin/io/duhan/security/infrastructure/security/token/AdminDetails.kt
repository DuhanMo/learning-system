package io.duhan.security.infrastructure.security.token

data class AdminDetails(
    val id: Long,
    val name: String,
    val email: String,
    val department: String,
)
