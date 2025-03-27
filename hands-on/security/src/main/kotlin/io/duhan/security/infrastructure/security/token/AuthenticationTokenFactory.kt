package io.duhan.security.infrastructure.security.token

import io.duhan.security.application.dto.AuthCommand
import io.duhan.security.application.dto.AuthCommand.ApiKeyAuthCommand
import io.duhan.security.application.dto.AuthCommand.EmailAuthCommand
import io.duhan.security.application.dto.AuthCommand.SocialAuthCommand
import io.duhan.security.domain.UserType
import io.duhan.security.infrastructure.security.token.EmailAuthenticationToken.AdminEmailAuthenticationToken
import io.duhan.security.infrastructure.security.token.EmailAuthenticationToken.LecturerEmailAuthenticationToken
import io.duhan.security.infrastructure.security.token.EmailAuthenticationToken.MemberEmailAuthenticationToken
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.stereotype.Component

@Component
class AuthenticationTokenFactory {
    fun createToken(authCommand: AuthCommand): AbstractAuthenticationToken =
        when (authCommand) {
            is EmailAuthCommand -> createEmailAuthenticationToken(authCommand)
            is SocialAuthCommand -> createMemberSocialAuthenticationToken(authCommand)
            is ApiKeyAuthCommand -> throw IllegalArgumentException("미구현 인증방식")
        }

    private fun createEmailAuthenticationToken(authCredentials: EmailAuthCommand): EmailAuthenticationToken =
        when (authCredentials.userType) {
            UserType.ADMIN ->
                AdminEmailAuthenticationToken(
                    email = authCredentials.email,
                    password = authCredentials.password,
                )

            UserType.LECTURER ->
                LecturerEmailAuthenticationToken(
                    email = authCredentials.email,
                    password = authCredentials.password,
                )

            UserType.MEMBER ->
                MemberEmailAuthenticationToken(
                    email = authCredentials.email,
                    password = authCredentials.password,
                )
        }

    private fun createMemberSocialAuthenticationToken(authCredentials: SocialAuthCommand): MemberSocialAuthenticationToken =
        MemberSocialAuthenticationToken(
            authCredentials.socialToken,
            authCredentials.provider,
        )
}
