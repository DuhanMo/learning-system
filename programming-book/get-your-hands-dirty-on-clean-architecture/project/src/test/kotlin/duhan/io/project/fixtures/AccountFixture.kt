package duhan.io.project.fixtures

import duhan.io.project.application.domain.model.Account.AccountId
import duhan.io.project.application.domain.model.Activity
import duhan.io.project.application.domain.model.Activity.ActivityId
import duhan.io.project.application.domain.model.ActivityWindow
import java.time.LocalDateTime

fun createAccount(
    id: AccountId? = AccountId(1L),
    baselineBalance: duhan.io.project.application.domain.model.Money = duhan.io.project.application.domain.model.Money.of(
        1_000L
    ),
    activityWindow: ActivityWindow = ActivityWindow(),
): duhan.io.project.application.domain.model.Account = duhan.io.project.application.domain.model.Account(
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
    money: duhan.io.project.application.domain.model.Money = duhan.io.project.application.domain.model.Money.of(1_000L),
): Activity = Activity(
    id = id,
    ownerAccountId = ownerAccountId,
    sourceAccountId = sourceAccountId,
    targetAccountId = targetAccountId,
    timestamp = timestamp,
    money = money,
)