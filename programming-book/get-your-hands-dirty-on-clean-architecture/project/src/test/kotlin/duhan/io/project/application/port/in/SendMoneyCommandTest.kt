package duhan.io.project.application.port.`in`

import duhan.io.project.application.domain.model.Account.AccountId
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
                money = duhan.io.project.application.domain.model.Money(BigDecimal("-10")),
            )
        }
    }
}