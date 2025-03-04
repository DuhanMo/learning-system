package duhan.io.project.application.domain.model

import duhan.io.project.application.domain.model.Account.AccountId
import duhan.io.project.fixtures.createActivity
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ActivityWindowTest {
    @Test
    fun calculatesStartTimestamp() {
        val window = ActivityWindow(
            createActivity(timestamp = startDate()),
            createActivity(timestamp = inBetweenDate()),
            createActivity(timestamp = endDate())
        )

        assertThat(window.startTimestamp).isEqualTo(startDate())
    }

    @Test
    fun calculatesEndTimestamp() {
        val window = ActivityWindow(
            createActivity(timestamp = startDate()),
            createActivity(timestamp = inBetweenDate()),
            createActivity(timestamp = endDate())
        )

        assertThat(window.endTimestamp).isEqualTo(endDate())
    }

    @Test
    fun calculatesBalance() {
        val accountId1 = AccountId(1L)
        val accountId2 = AccountId(2L)

        val window = ActivityWindow(
            createActivity(
                sourceAccountId = accountId1,
                targetAccountId = accountId2,
                money = Money.of(999L),
            ),
            createActivity(
                sourceAccountId = accountId1,
                targetAccountId = accountId2,
                money = Money.of(1L),
            ),
            createActivity(
                sourceAccountId = accountId2,
                targetAccountId = accountId1,
                money = Money.of(500L),
            ),
        )

        assertThat(window.calculateBalance(accountId1)).isEqualTo(Money.of(-500L))
        assertThat(window.calculateBalance(accountId2)).isEqualTo(
            Money.of(
                500L
            )
        )

    }

    private fun startDate(): LocalDateTime = LocalDateTime.of(2019, 8, 3, 0, 0)

    private fun inBetweenDate(): LocalDateTime = LocalDateTime.of(2019, 8, 4, 0, 0)

    private fun endDate(): LocalDateTime = LocalDateTime.of(2019, 8, 5, 0, 0)
}