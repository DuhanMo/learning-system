package io.duhan.security.domain

enum class UserType {
    ADMIN,
    LECTURER,
    MEMBER,
    ;

    fun roleName() = "ROLE_${this.name}"
}
