package duhan.io.project.account.fixtures

import duhan.io.project.account.application.domain.model.Account
import duhan.io.project.account.application.domain.model.Account.AccountId
import duhan.io.project.account.application.domain.model.Activity
import duhan.io.project.account.application.domain.model.Activity.ActivityId
import duhan.io.project.account.application.domain.model.ActivityWindow
import duhan.io.project.account.application.domain.model.Money
import java.time.LocalDateTime

fun createAccount(
    id: AccountId? = AccountId(1L),
    baselineBalance: Money = Money.of(1_000L),
    activityWindow: ActivityWindow = ActivityWindow(),
): Account = Account(
    id = id,
    baselineBalance = baselineBalance,
    activityWindow = activityWindow,
)


fun createActivity(
    id: ActivityId? = ActivityId(1L),
    ownerAccountId: AccountId = AccountId(1L),
    sourceAccountId: AccountId = AccountId(1L),
    targetAccountId: AccountId = AccountId(2L),
    timestamp: LocalDateTime = LocalDateTime.now(),
    money: Money = Money.of(1_000L),
): Activity = Activity(
    id = id,
    ownerAccountId = ownerAccountId,
    sourceAccountId = sourceAccountId,
    targetAccountId = targetAccountId,
    timestamp = timestamp,
    money = money,
)