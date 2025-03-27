package io.duhan.security.infrastructure.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class MemberSocialAuthenticationToken : AbstractAuthenticationToken {
    private val principal: String // social email? id?
    private val credentials: String // social access token
    val provider: String
    val socialId: String?

    constructor(token: String, provider: String, socialId: String? = null) : super(null) {
        this.principal = provider
        this.credentials = token
        this.provider = provider
        this.socialId = socialId
        this.isAuthenticated = false
    }

    constructor(
        username: String,
        authorities: Collection<GrantedAuthority>,
        provider: String,
        details: Any?,
    ) : super(authorities) {
        this.principal = username
        this.credentials = ""
        this.provider = provider
        this.socialId = null
        this.details = details
        this.isAuthenticated = true
    }

    override fun getCredentials(): Any = credentials

    override fun getPrincipal(): Any = principal
}
