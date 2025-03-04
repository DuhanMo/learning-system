package duhan.io.project.application.port.`in`

import duhan.io.project.application.domain.model.Account.AccountId
import duhan.io.project.application.domain.model.Money

interface GetAccountBalanceUseCase {
    fun getAccountBalance(query: GetAccountBalanceQuery): Money

    data class GetAccountBalanceQuery(val accountId: AccountId)
}

