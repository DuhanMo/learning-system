package duhan.io.project.account.application.domain.model

import duhan.io.project.account.application.domain.model.Account.AccountId
import java.time.LocalDateTime

class Activity(
    val id: ActivityId? = null,
    val ownerAccountId: AccountId,
    val sourceAccountId: AccountId,
    val targetAccountId: AccountId,
    val timestamp: LocalDateTime,
    val money: Money,
) {
    @JvmInline
    value class ActivityId(val value: Long)
}