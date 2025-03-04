package duhan.io.project.application.port.out

import duhan.io.project.application.domain.model.Account
import duhan.io.project.application.domain.model.Account.AccountId
import java.time.LocalDateTime

interface LoadAccountPort {
    fun loadAccount(
        accountId: AccountId,
        baselineDate: LocalDateTime,
    ): Account
}