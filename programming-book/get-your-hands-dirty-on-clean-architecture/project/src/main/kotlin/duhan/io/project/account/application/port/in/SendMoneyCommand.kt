package duhan.io.project.account.application.port.`in`

import duhan.io.project.account.application.domain.model.Account.AccountId
import duhan.io.project.account.application.domain.model.Money

data class SendMoneyCommand(
    val sourceAccountId: AccountId,
    val targetAccountId: AccountId,
    val money: Money,
) {
    init {
        require(money.isPositive()) { "money must be greater than zero" }
    }
}
