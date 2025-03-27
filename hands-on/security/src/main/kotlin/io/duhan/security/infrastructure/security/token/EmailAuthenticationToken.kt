package io.duhan.security.infrastructure.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

sealed class EmailAuthenticationToken(
    private val principal: String,
    private val credentials: String?,
    authorities: Collection<GrantedAuthority>?,
    details: Any? = null,
) : AbstractAuthenticationToken(authorities) {
    init {
        this.details = details
        this.isAuthenticated = authorities != null
    }

    constructor(
        email: String,
        password: String,
    ) : this(email, password, null)

    constructor(
        email: String,
        authorities: Collection<GrantedAuthority>,
        details: Any? = null,
    ) : this(email, null, authorities, details)

    override fun getCredentials(): Any? = credentials

    override fun getPrincipal(): Any = principal

    class AdminEmailAuthenticationToken : EmailAuthenticationToken {
        constructor(email: String, password: String) : super(email, password)
        constructor(email: String, authorities: Collection<GrantedAuthority>, details: Any? = null) :
            super(email, authorities, details)
    }

    class LecturerEmailAuthenticationToken : EmailAuthenticationToken {
        constructor(email: String, password: String) : super(email, password)
        constructor(email: String, authorities: Collection<GrantedAuthority>, details: Any? = null) :
            super(email, authorities, details)
    }

    class MemberEmailAuthenticationToken : EmailAuthenticationToken {
        constructor(email: String, password: String) : super(email, password)
        constructor(email: String, authorities: Collection<GrantedAuthority>, details: Any? = null) :
            super(email, authorities, details)
    }
}
