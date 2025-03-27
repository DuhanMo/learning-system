package io.duhan.security.infrastructure.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken : AbstractAuthenticationToken {
    private val principal: String // username
    private val credentials: String // jwt

    constructor(token: String) : super(null) {
        this.principal = ""
        this.credentials = token
        this.isAuthenticated = false
    }

    constructor(
        username: String,
        token: String,
        authorities: Collection<GrantedAuthority>,
        details: Any?,
    ) : super(authorities) {
        this.principal = username
        this.credentials = token
        this.details = details
        this.isAuthenticated = true
    }

    override fun getCredentials(): Any = credentials

    override fun getPrincipal(): Any = principal
}
