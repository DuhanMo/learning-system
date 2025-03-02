package duhan.io.project.account.application.domain.model

import java.time.LocalDateTime

class Account(
    val id: AccountId? = null,
    val baselineBalance: Money,
    val activityWindow: ActivityWindow,
) {
    fun calculateBalance(): Money = Money.add(baselineBalance, activityWindow.calculateBalance(id!!))

    fun withdraw(money: Money, targetAccountId: AccountId): Boolean {
        if (!mayWithdraw(money)) {
            return false
        }
        val activity = Activity(
            ownerAccountId = id!!,
            sourceAccountId = id,
            targetAccountId = targetAccountId,
            timestamp = LocalDateTime.now(),
            money = money,
        )
        activityWindow.addActivity(activity)
        return true
    }

    fun deposit(money: Money, sourceAccountId: AccountId): Boolean {
        val deposit = Activity(
            ownerAccountId = id!!,
            sourceAccountId = sourceAccountId,
            targetAccountId = id,
            timestamp = LocalDateTime.now(),
            money = money,
        )
        activityWindow.addActivity(deposit)
        return true
    }

    private fun mayWithdraw(money: Money): Boolean = (calculateBalance() - money).isPositiveOrZero()


    companion object {
        fun withoutId(
            baselineBalance: Money,
            activityWindow: ActivityWindow,
        ): Account = Account(
            baselineBalance = baselineBalance,
            activityWindow = activityWindow,
        )

        fun withId(
            accountId: AccountId,
            baselineBalance: Money,
            activityWindow: ActivityWindow,
        ): Account = Account(
            id = accountId,
            baselineBalance = baselineBalance,
            activityWindow = activityWindow,
        )
    }

    @JvmInline
    value class AccountId(val value: Long)
}