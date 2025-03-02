package duhan.io.project.account.adapter.out.persistence

import duhan.io.project.account.application.domain.model.Account
import duhan.io.project.account.application.domain.model.Account.AccountId
import duhan.io.project.account.application.domain.model.Activity
import duhan.io.project.account.application.domain.model.Activity.ActivityId
import duhan.io.project.account.application.domain.model.ActivityWindow
import duhan.io.project.account.application.domain.model.Money
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
        id = activity.id?.value ?: 0L,
        timestamp = activity.timestamp,
        ownerAccountId = activity.ownerAccountId.value,
        sourceAccountId = activity.sourceAccountId.value,
        targetAccountId = activity.targetAccountId.value,
        amount = activity.money.amount.longValueExact(),
    )
}