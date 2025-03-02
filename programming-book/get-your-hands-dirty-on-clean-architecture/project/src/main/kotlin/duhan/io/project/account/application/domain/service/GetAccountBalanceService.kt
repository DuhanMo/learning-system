package duhan.io.project.account.application.domain.service

import duhan.io.project.account.application.domain.model.Money
import duhan.io.project.account.application.port.`in`.GetAccountBalanceUseCase
import duhan.io.project.account.application.port.out.LoadAccountPort
import java.time.LocalDateTime

class GetAccountBalanceService(private val loadAccountPort: LoadAccountPort): GetAccountBalanceUseCase {
    override fun getAccountBalance(query: GetAccountBalanceUseCase.GetAccountBalanceQuery) : Money =
        loadAccountPort.loadAccount(query.accountId, LocalDateTime.now()).calculateBalance()
}