package io.duhan.security.application.dto

import io.duhan.security.domain.UserType

sealed class AuthCommand {
    data class EmailAuthCommand(
        val email: String,
        val password: String,
        val userType: UserType,
    ) : AuthCommand()

    data class SocialAuthCommand(
        val provider: String,
        val socialToken: String,
    ) : AuthCommand()

    data class ApiKeyAuthCommand(
        val apiKey: String,
    ) : AuthCommand()
}
