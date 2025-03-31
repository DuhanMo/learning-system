package io.duhan.security.infrastructure.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken : AbstractAuthenticationToken {
    private val principal: String // jwt
    private val credentials: String = ""

    constructor(token: String) : super(null) {
        this.principal = token
        this.isAuthenticated = false
    }

    constructor(
        token: String,
        authorities: Collection<GrantedAuthority>,
        details: Any?,
    ) : super(authorities) {
        this.principal = token
        this.details = details
        this.isAuthenticated = true
    }

    override fun getCredentials(): Any = credentials

    override fun getPrincipal(): Any = principal
}
