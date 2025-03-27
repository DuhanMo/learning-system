package io.duhan.security.application

import io.duhan.security.application.dto.AuthCommand
import io.duhan.security.application.dto.AuthResult
import io.duhan.security.application.port.Authenticator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(private val authenticator: Authenticator) {
    @Transactional
    fun authenticate(command: AuthCommand): AuthResult = authenticator.authenticate(command)
}
