package duhan.io.project.adapter.out.persistence

import duhan.io.project.application.domain.model.Account.AccountId
import duhan.io.project.application.domain.model.Activity
import duhan.io.project.application.domain.model.Activity.ActivityId
import duhan.io.project.application.domain.model.ActivityWindow
import org.springframework.stereotype.Component

@Component
class AccountMapper {
    fun mapToDomainEntity(
        account: AccountJpaEntity,
        activities: List<ActivityJpaEntity>,
        withdrawalBalance: Long,
        depositBalance: Long,
    ): duhan.io.project.application.domain.model.Account {
        val baselineBalance = duhan.io.project.application.domain.model.Money.subtract(
            duhan.io.project.application.domain.model.Money.of(depositBalance),
            duhan.io.project.application.domain.model.Money.of(withdrawalBalance),
        )

        return duhan.io.project.application.domain.model.Account.withId(
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
            money = duhan.io.project.application.domain.model.Money.of(it.amount),
        )
    })

    fun mapToJpaEntity(activity: Activity): ActivityJpaEntity =
        ActivityJpaEntity(
            id = activity.id?.value ?: 0L,
            timestamp = activity.timestamp,
            ownerAccountId = activity.ownerAccountId.value,
            sourceAccountId = activity.sourceAccountId.value,
            targetAccountId = activity.targetAccountId.value,
            amount = activity.money.amount.longValueExact(),
        )
}