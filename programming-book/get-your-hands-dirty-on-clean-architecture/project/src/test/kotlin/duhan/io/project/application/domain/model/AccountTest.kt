package duhan.io.project.application.domain.model

import duhan.io.project.application.domain.model.Account.AccountId
import duhan.io.project.fixtures.createAccount
import duhan.io.project.fixtures.createActivity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AccountTest {

    @Test
    fun calculatesBalance() {
        // given
        val accountId = AccountId(1L)
        val account = createAccount(
            id = accountId,
            baselineBalance = Money.of(555L),
            activityWindow = ActivityWindow(
                createActivity(sourceAccountId = AccountId(43L), targetAccountId = accountId, money = Money.of(999L)),
                createActivity(sourceAccountId = AccountId(13L), targetAccountId = accountId, money = Money.of(1L)),
            )
        )

        // when
        val balance = account.calculateBalance()

        // then
        assertThat(balance).isEqualTo(Money.of(1_555L))
    }

    @Test
    fun withdrawalSucceeds() {
        // given
        val accountId = AccountId(1L)
        val account = createAccount(
            id = accountId,
            baselineBalance = Money.of(555L),
            activityWindow = ActivityWindow(
                createActivity(sourceAccountId = AccountId(43L), targetAccountId = accountId, money = Money.of(999L)),
                createActivity(sourceAccountId = AccountId(13L), targetAccountId = accountId, money = Money.of(1L)),
            )
        )

        // when
        val success = account.withdraw(
            money = Money.of(555L),
            targetAccountId = AccountId(28L)
        )

        // then
        assertThat(success).isTrue
        assertThat(account.activityWindow.activities).hasSize(3)
        assertThat(account.calculateBalance()).isEqualTo(Money.of(1_000L))
    }

    @Test
    fun withdrawalFailure() {
        // given
        val accountId = AccountId(1L)
        val account = createAccount(
            id = accountId,
            baselineBalance = Money.of(555L),
            activityWindow = ActivityWindow(
                createActivity(sourceAccountId = AccountId(43L), targetAccountId = accountId, money = Money.of(999L)),
                createActivity(sourceAccountId = AccountId(13L), targetAccountId = accountId, money = Money.of(1L)),
            )
        )

        // when
        val success = account.withdraw(Money.of(1_556L), AccountId(99L))

        // then
        assertThat(success).isFalse
        assertThat(account.activityWindow.activities).hasSize(2)
        assertThat(account.calculateBalance()).isEqualTo(Money.of(1_555L))
    }

    @Test
    fun depositSuccess() {
        // given
        val accountId = AccountId(1L)
        val account = createAccount(
            id = accountId,
            baselineBalance = Money.of(555L),
            activityWindow = ActivityWindow(
                createActivity(sourceAccountId = AccountId(43L), targetAccountId = accountId, money = Money.of(999L)),
                createActivity(sourceAccountId = AccountId(13L), targetAccountId = accountId, money = Money.of(1L)),
            )
        )

        // when
        val success = account.deposit(Money.of(445L), AccountId(99L))

        // then
        assertThat(success).isTrue()
        assertThat(account.activityWindow.activities).hasSize(3)
        assertThat(account.calculateBalance()).isEqualTo(Money.of(2000L))
    }
}
