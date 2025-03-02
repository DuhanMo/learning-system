package duhan.io.project.account.application.port.`in`

import duhan.io.project.account.application.domain.model.Account.AccountId
import duhan.io.project.account.application.domain.model.Money
import java.math.BigDecimal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SendMoneyCommandTest {
    @Test
    fun moneyShouldPositive() {
        assertThrows<IllegalArgumentException> {
            SendMoneyCommand(
                sourceAccountId = AccountId(1L),
                targetAccountId = AccountId(33L),
                money = Money(BigDecimal("-10")),
            )
        }
    }
}