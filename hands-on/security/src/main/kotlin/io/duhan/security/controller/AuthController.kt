package io.duhan.security.controller

import io.duhan.security.application.AuthService
import io.duhan.security.application.dto.AuthCommand.EmailAuthCommand
import io.duhan.security.application.dto.AuthCommand.RefreshTokenAuthCommand
import io.duhan.security.application.dto.TokenResult
import io.duhan.security.controller.dto.EmailLoginRequest
import io.duhan.security.controller.dto.RefreshAuthRequest
import io.duhan.security.domain.UserType.ADMIN
import io.duhan.security.domain.UserType.LECTURER
import io.duhan.security.domain.UserType.MEMBER
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService,
    private val clientHttpRequestFactorySettings: ClientHttpRequestFactorySettings,
) {
    @PostMapping("/api/v1/admins/login")
    fun adminLogin(
        @RequestBody request: EmailLoginRequest,
    ): TokenResult =
        authService.authenticate(
            EmailAuthCommand(
                email = request.email,
                password = request.password,
                userType = ADMIN,
            ),
        )

    @PostMapping("/api/v1/lecturers/login")
    fun lecturerLogin(
        @RequestBody request: EmailLoginRequest,
    ): TokenResult =
        authService.authenticate(
            EmailAuthCommand(
                email = request.email,
                password = request.password,
                userType = LECTURER,
            ),
        )

    @PostMapping("/api/v1/members/login")
    fun memberLogin(
        @RequestBody request: EmailLoginRequest,
    ): TokenResult =
        authService.authenticate(
            EmailAuthCommand(
                email = request.email,
                password = request.password,
                userType = MEMBER,
            ),
        )

    @PostMapping("/api/v1/admins/refresh")
    fun refreshAdmin(
        @RequestBody request: RefreshAuthRequest,
    ) {
        authService.authenticate(
            RefreshTokenAuthCommand(
                refreshToken = request.refreshToken,
                userType = ADMIN,
            )
        )
    }

    @PostMapping("/api/v1/lecturers/refresh")
    fun refreshLecturer(
        @RequestBody request: RefreshAuthRequest,
    ) {
        authService.authenticate(
            RefreshTokenAuthCommand(
                refreshToken = request.refreshToken,
                userType = LECTURER,
            )
        )
    }

    @PostMapping("/api/v1/members/refresh")
    fun refreshMember(
        @RequestBody request: RefreshAuthRequest,
    ) {
        authService.authenticate(
            RefreshTokenAuthCommand(
                refreshToken = request.refreshToken,
                userType = MEMBER,
            )
        )
    }
}
