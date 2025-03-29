package io.duhan.security.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class AdminRole(
    @Id @GeneratedValue
    val id: Long = 0L,
    val adminId: Long,
    val role: String,
)
