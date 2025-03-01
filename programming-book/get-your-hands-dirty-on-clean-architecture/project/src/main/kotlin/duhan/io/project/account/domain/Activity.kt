package duhan.io.project.account.domain

import duhan.io.project.account.domain.Account.AccountId
import java.time.LocalDateTime

class Activity(
    val id: ActivityId = ActivityId(0L),
    val ownerAccountId: AccountId,
    val sourceAccountId: AccountId,
    val targetAccountId: AccountId,
    val timestamp: LocalDateTime,
    val money: Money,
) {
    @JvmInline
    value class ActivityId(val value: Long)
}