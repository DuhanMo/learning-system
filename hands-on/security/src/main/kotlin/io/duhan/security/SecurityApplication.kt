package io.duhan.security

import io.duhan.security.domain.Admin
import io.duhan.security.domain.AdminRole
import io.duhan.security.domain.Lecturer
import io.duhan.security.domain.Member
import io.duhan.security.infrastructure.persistence.support.AdminJpaRepository
import io.duhan.security.infrastructure.persistence.support.AdminRoleJpaRepository
import io.duhan.security.infrastructure.persistence.support.LecturerJpaRepository
import io.duhan.security.infrastructure.persistence.support.MemberJpaRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@SpringBootApplication
class SecurityApplication

fun main(args: Array<String>) {
    runApplication<SecurityApplication>(*args)
}

@Component
class DataInit(
    private val adminJpaRepository: AdminJpaRepository,
    private val adminRoleJpaRepository: AdminRoleJpaRepository,
    private val lecturerJpaRepository: LecturerJpaRepository,
    private val memberJpaRepository: MemberJpaRepository,
    private val passwordEncoder: PasswordEncoder,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val admin =
            adminJpaRepository.save(
                Admin(
                    email = "admin@test.com",
                    password = passwordEncoder.encode("test"),
                    name = "김관리",
                    department = "경무과",
                ),
            )
        adminRoleJpaRepository.save(
            AdminRole(
                adminId = admin.id,
                role = "ROLE_ADMIN",
            ),
        )

        adminRoleJpaRepository.save(
            AdminRole(
                adminId = admin.id,
                role = "CREATE_MEMBER",
            ),
        )

        adminRoleJpaRepository.save(
            AdminRole(
                adminId = admin.id,
                role = "DELETE_MEMBER",
            ),
        )

        lecturerJpaRepository.save(
            Lecturer(
                email = "lecturer@test.com",
                password = passwordEncoder.encode("test"),
                name = "박강사",
                subject = "물리",
            ),
        )

        memberJpaRepository.save(
            Member(
                email = "member@test.com",
                password = passwordEncoder.encode("test"),
                name = "최멤버",
                grade = "VIP",
            ),
        )
    }
}
