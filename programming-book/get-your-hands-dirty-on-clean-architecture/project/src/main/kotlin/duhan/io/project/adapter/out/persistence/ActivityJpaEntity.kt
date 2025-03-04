package duhan.io.project.adapter.out.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "activity")
class ActivityJpaEntity(
    @Id
    @GeneratedValue
    val id: Long,

    @Column
    val timestamp: LocalDateTime,

    @Column
    val ownerAccountId: Long,

    @Column
    val sourceAccountId: Long,

    @Column
    val targetAccountId: Long,

    @Column
    val amount: Long,
)