package io.duhan.security.controller.dto

data class EmailLoginRequest(
    val email: String,
    val password: String,
)
