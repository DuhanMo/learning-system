package io.duhan.security.infrastructure.security.token

import io.duhan.security.domain.UserType
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class RefreshTokenAuthenticationToken : AbstractAuthenticationToken {
    private val principal: String
    private val credentials: String = ""
    private val userType: UserType

    constructor(token: String, userType: UserType) : super(null) {
        this.principal = token
        this.isAuthenticated = false
        this.userType = userType
    }

    constructor(
        token: String,
        authorities: Collection<GrantedAuthority>,
        details: Any?,
        userType: UserType,
    ) : super(authorities) {
        this.principal = token
        this.details = details
        this.userType = userType
        this.isAuthenticated = true
    }

    override fun getCredentials(): Any = credentials

    override fun getPrincipal(): Any = principal
}
