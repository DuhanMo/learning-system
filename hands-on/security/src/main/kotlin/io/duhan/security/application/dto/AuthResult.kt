package io.duhan.security.application.dto

sealed class AuthResult {
    data class AdminAuthResult(
        val id: Long,
        val name: String,
        val email: String,
        val department: String,
        val roles: List<String>,
    ) : AuthResult()

    data class LecturerAuthResult(
        val id: Long,
        val name: String,
        val email: String,
        val subject: String,
        val roles: List<String>,
    ) : AuthResult()

    data class MemberAuthResult(
        val id: Long,
        val name: String,
        val grade: String,
        val roles: List<String>,
    ) : AuthResult()
}
