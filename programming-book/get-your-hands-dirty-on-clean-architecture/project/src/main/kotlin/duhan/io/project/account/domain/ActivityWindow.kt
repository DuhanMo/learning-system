package duhan.io.project.account.domain

import duhan.io.project.account.domain.Account.AccountId
import java.time.LocalDateTime

class ActivityWindow(activities: List<Activity>) {

    constructor(vararg activities: Activity) : this(activities.toList())

    private val _activities: MutableList<Activity> = activities.toMutableList()

    val activities: List<Activity>
        get() = _activities.toList()

    val startTimestamp: LocalDateTime
        get() = _activities.minOfOrNull { it.timestamp } ?: throw IllegalStateException("not found activity")

    val endTimestamp: LocalDateTime
        get() = _activities.maxOfOrNull { it.timestamp } ?: throw IllegalStateException("not found activity")

    fun calculateBalance(accountId: AccountId): Money {
        val depositBalance = calculateDepositBalance(accountId)
        val withdrawalBalance = calculateWithdrawalBalance(accountId)

        return depositBalance.plus(withdrawalBalance.negate())
    }

    private fun calculateDepositBalance(accountId: AccountId): Money =
        _activities
            .filter { it.targetAccountId == accountId }
            .map { it.money }
            .fold(Money.ZERO, Money::add)

    private fun calculateWithdrawalBalance(accountId: AccountId): Money =
        _activities
            .filter { it.sourceAccountId == accountId }
            .map { it.money }
            .fold(Money.ZERO, Money::add)

    fun addActivity(activity: Activity) {
        _activities.add(activity)
    }
}