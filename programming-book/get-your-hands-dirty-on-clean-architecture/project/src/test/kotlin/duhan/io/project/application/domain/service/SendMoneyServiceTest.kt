package duhan.io.project.application.domain.service

import duhan.io.project.application.domain.model.Account.AccountId
import duhan.io.project.application.domain.model.Money
import duhan.io.project.application.port.`in`.SendMoneyCommand
import duhan.io.project.application.port.out.AccountLock
import duhan.io.project.application.port.out.LoadAccountPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.Long.Companion.MAX_VALUE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SendMoneyServiceTest {
    private val loadAccountPort = mockk<LoadAccountPort>()
    private val accountLock = mockk<AccountLock>(relaxed = true)
    private val updateAccountStatePort =
        mockk<duhan.io.project.application.port.out.UpdateAccountStatePort>(relaxed = true)

    private val sendMoneyService = SendMoneyService(
        loadAccountPort,
        accountLock,
        updateAccountStatePort,
        moneyTransferProperties(),
    )

    @Test
    fun givenWithdrawalFails_thenOnlySourceAccountIsLockedAndReleased() {
        // given
        val sourceAccountId = AccountId(41L)
        val sourceAccount = givenAnAccountWithId(sourceAccountId)

        val targetAccountId = AccountId(42L)
        val targetAccount = givenAnAccountWithId(targetAccountId)

        givenWithdrawalWillFail(sourceAccount)
        givenDepositWillSucceed(targetAccount)

        val command = SendMoneyCommand(
            sourceAccountId,
            targetAccountId,
            Money.of(300L)
        )

        // when
        val success = sendMoneyService.sendMoney(command)

        // then
        assertThat(success).isFalse()

        verify { accountLock.lockAccount(sourceAccountId) }
        verify { accountLock.releaseAccount(sourceAccountId) }
        verify(exactly = 0) { accountLock.lockAccount(targetAccountId) }
    }

    @Test
    fun transactionSucceeds() {
        // given
        val sourceAccount = givenSourceAccount()
        val targetAccount = givenTargetAccount()

        givenWithdrawalWillSucceed(sourceAccount)
        givenDepositWillSucceed(targetAccount)

        val money = Money.of(500L)

        val command = SendMoneyCommand(
            sourceAccount.id!!,
            targetAccount.id!!,
            money
        )

        // when
        val success = sendMoneyService.sendMoney(command)

        // then
        assertThat(success).isTrue()

        val sourceAccountId = sourceAccount.id!!
        val targetAccountId = targetAccount.id!!

        verify { accountLock.lockAccount(sourceAccountId) }
        verify { sourceAccount.withdraw(money, targetAccountId) }
        verify { accountLock.releaseAccount(sourceAccountId) }

        verify { accountLock.lockAccount(targetAccountId) }
        verify { targetAccount.deposit(money, sourceAccountId) }
        verify { accountLock.releaseAccount(targetAccountId) }

        thenAccountsHaveBeenUpdated(listOf(sourceAccountId, targetAccountId))
    }

    private fun thenAccountsHaveBeenUpdated(accountIds: List<AccountId>) {
        val accountsSlot = mutableListOf<duhan.io.project.application.domain.model.Account>()

        verify(exactly = accountIds.size) {
            updateAccountStatePort.updateActivities(capture(accountsSlot))
        }

        val updatedAccountIds = accountsSlot.mapNotNull { it.id }

        for (accountId in accountIds) {
            assertThat(updatedAccountIds).contains(accountId)
        }
    }

    private fun givenDepositWillSucceed(account: duhan.io.project.application.domain.model.Account) {
        every { account.deposit(any(), any()) } returns true
    }

    private fun givenWithdrawalWillFail(account: duhan.io.project.application.domain.model.Account) {
        every { account.withdraw(any(), any()) } returns false
    }

    private fun givenWithdrawalWillSucceed(account: duhan.io.project.application.domain.model.Account) {
        every { account.withdraw(any(), any()) } returns true
    }

    private fun givenTargetAccount(): duhan.io.project.application.domain.model.Account =
        givenAnAccountWithId(AccountId(42L))

    private fun givenSourceAccount(): duhan.io.project.application.domain.model.Account =
        givenAnAccountWithId(AccountId(41L))

    private fun givenAnAccountWithId(id: AccountId): duhan.io.project.application.domain.model.Account {
        val account = mockk<duhan.io.project.application.domain.model.Account>()
        every { account.id } returns id
        every { loadAccountPort.loadAccount(id, any()) } returns account
        return account
    }

    private fun moneyTransferProperties(): MoneyTransferProperties =
        MoneyTransferProperties(Money.of(MAX_VALUE))

}