package duhan.io.project.account.adapter.out.persistence

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "account")
class AccountJpaEntity(
    @Id
    @GeneratedValue
    val id: Long,
)