package duhan.io.project.account.application.port.out

import duhan.io.project.account.domain.Account
import duhan.io.project.account.domain.Account.AccountId
import java.time.LocalDateTime

interface LoadAccountPort {
    fun loadAccount(
        accountId: AccountId,
        baselineDate: LocalDateTime,
    ): Account
}