package duhan.io.project.account.adapter.`in`.web

import duhan.io.project.account.application.port.`in`.SendMoneyCommand
import duhan.io.project.account.application.port.`in`.SendMoneyUseCase
import duhan.io.project.account.domain.Account.AccountId
import duhan.io.project.account.domain.Money
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class SendMoneyController(
    private val sendMoneyUseCase: SendMoneyUseCase,
) {
    @PostMapping("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
    fun sendMoney(
        @PathVariable sourceAccountId: Long,
        @PathVariable targetAccountId: Long,
        @PathVariable amount: Long,
    ) {
        val command = SendMoneyCommand(
            sourceAccountId = AccountId(sourceAccountId),
            targetAccountId = AccountId(targetAccountId),
            money = Money.of(amount)
        )
        sendMoneyUseCase.sendMoney(command)
    }
}