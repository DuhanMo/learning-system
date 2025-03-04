package duhan.io.project.application.port.`in`

import duhan.io.project.application.domain.model.Account.AccountId
import duhan.io.project.application.domain.model.Money

data class SendMoneyCommand(
    val sourceAccountId: AccountId,
    val targetAccountId: AccountId,
    val money: Money,
) {
    init {
        require(money.isPositive()) { "money must be greater than zero" }
    }
}
