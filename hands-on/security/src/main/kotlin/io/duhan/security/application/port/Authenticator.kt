package io.duhan.security.application.port

import io.duhan.security.application.dto.AuthCommand
import io.duhan.security.application.dto.AuthResult

interface Authenticator {
    fun authenticate(authCommand: AuthCommand): AuthResult
}
