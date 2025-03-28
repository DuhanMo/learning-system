package io.duhan.security.infrastructure.security.adapter

import io.duhan.security.application.dto.AuthCommand
import io.duhan.security.application.dto.AuthResult
import io.duhan.security.application.port.Authenticator
import io.duhan.security.infrastructure.security.token.AuthenticationTokenFactory
import io.duhan.security.infrastructure.security.token.UserDetails
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.stereotype.Component

@Component
class SpringSecurityAuthenticator(
    private val authenticationManager: AuthenticationManager,
    private val authenticationTokenFactory: AuthenticationTokenFactory,
) : Authenticator {
    override fun authenticate(authCommand: AuthCommand): AuthResult {
        val authentication = authenticationManager.authenticate(authenticationTokenFactory.createToken(authCommand))
        return AuthResult(
            id = (authentication.details as UserDetails).id,
            roles = authentication.authorities.map { it.authority },
        )
    }
}
