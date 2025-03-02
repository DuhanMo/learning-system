package duhan.io.project.account.application.port.`in`

import duhan.io.project.account.application.domain.model.Account.AccountId
import duhan.io.project.account.application.domain.model.Money

interface GetAccountBalanceUseCase {
    fun getAccountBalance(query: GetAccountBalanceQuery): Money

    data class GetAccountBalanceQuery(val accountId: AccountId)
}

