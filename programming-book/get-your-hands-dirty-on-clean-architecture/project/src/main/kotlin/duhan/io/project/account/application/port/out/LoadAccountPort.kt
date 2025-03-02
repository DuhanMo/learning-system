package duhan.io.project.account.application.port.out

import duhan.io.project.account.application.domain.model.Account
import duhan.io.project.account.application.domain.model.Account.AccountId
import java.time.LocalDateTime

interface LoadAccountPort {
    fun loadAccount(
        accountId: AccountId,
        baselineDate: LocalDateTime,
    ): Account
}