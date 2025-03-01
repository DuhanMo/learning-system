package duhan.io.project.account.adapter.out.persistence

import duhan.io.project.account.domain.Account
import duhan.io.project.account.domain.Account.AccountId
import duhan.io.project.account.domain.Activity
import duhan.io.project.account.domain.Activity.ActivityId
import duhan.io.project.account.domain.ActivityWindow
import duhan.io.project.account.domain.Money
import org.springframework.stereotype.Component

@Component
class AccountMapper {
    fun mapToDomainEntity(
        account: AccountJpaEntity,
        activities: List<ActivityJpaEntity>,
        withdrawalBalance: Long,
        depositBalance: Long,
    ): Account {
        val baselineBalance = Money.subtract(
            Money.of(depositBalance),
            Money.of(withdrawalBalance),
        )

        return Account.withId(
            accountId = AccountId(account.id),
            baselineBalance = baselineBalance,
            activityWindow = mapToActivityWindow(activities),
        )
    }

    fun mapToActivityWindow(activities: List<ActivityJpaEntity>): ActivityWindow = ActivityWindow(activities.map {
        Activity(
            id = ActivityId(it.id),
            ownerAccountId = AccountId(it.ownerAccountId),
            sourceAccountId = AccountId(it.sourceAccountId),
            targetAccountId = AccountId(it.targetAccountId),
            timestamp = it.timestamp,
            money = Money.of(it.amount),
        )
    })

    fun mapToJpaEntity(activity: Activity): ActivityJpaEntity = ActivityJpaEntity(
        id = activity.id.value,
        timestamp = activity.timestamp,
        ownerAccountId = activity.ownerAccountId.value,
        sourceAccountId = activity.sourceAccountId.value,
        targetAccountId = activity.targetAccountId.value,
        amount = activity.money.amount.longValueExact(),
    )
}