package io.duhan.security.controller

import io.duhan.security.application.AuthService
import io.duhan.security.application.dto.AuthCommand.EmailAuthCommand
import io.duhan.security.application.dto.TokenResult
import io.duhan.security.domain.UserType.ADMIN
import io.duhan.security.domain.UserType.LECTURER
import io.duhan.security.domain.UserType.MEMBER
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/admin/login")
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

    @PostMapping("/lecturer/login")
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

    @PostMapping("/member/login")
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
}
