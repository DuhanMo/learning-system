package io.duhan.security.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Admin(
    @Id @GeneratedValue
    val id: Long = 0L,
    val name: String,
    val email: String,
    val password: String,
    val department: String,
)
