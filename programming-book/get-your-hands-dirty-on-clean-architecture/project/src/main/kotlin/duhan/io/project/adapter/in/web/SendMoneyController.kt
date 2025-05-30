package duhan.io.project.adapter.`in`.web

import duhan.io.project.application.domain.model.Account.AccountId
import duhan.io.project.application.port.`in`.SendMoneyCommand
import duhan.io.project.application.port.`in`.SendMoneyUseCase
import duhan.io.project.common.WebAdapter
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@WebAdapter
@RestController
class SendMoneyController(private val sendMoneyUseCase: SendMoneyUseCase) {
    @PostMapping("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
    fun sendMoney(
        @PathVariable sourceAccountId: Long,
        @PathVariable targetAccountId: Long,
        @PathVariable amount: Long,
    ) {
        sendMoneyUseCase.sendMoney(
            SendMoneyCommand(
                sourceAccountId = AccountId(sourceAccountId),
                targetAccountId = AccountId(targetAccountId),
                money = duhan.io.project.application.domain.model.Money.of(amount),
            )
        )
    }
}