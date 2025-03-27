package io.duhan.security.controller

data class EmailLoginRequest(
    val email: String,
    val password: String,
)
