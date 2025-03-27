package io.duhan.security.application.port

import io.duhan.security.domain.Lecturer

interface LecturerRepository {
    fun findByEmail(email: String): Lecturer?
}
