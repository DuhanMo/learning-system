package duhan.io.project.account.application.service

import duhan.io.project.account.application.port.`in`.SendMoneyCommand
import duhan.io.project.account.application.port.`in`.SendMoneyUseCase
import duhan.io.project.account.application.port.out.AccountLock
import duhan.io.project.account.application.port.out.LoadAccountPort
import duhan.io.project.account.application.port.out.UpdateAccountStatePort
import java.time.LocalDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SendMoneyService(
    private val loadAccountPort: LoadAccountPort,
    private val accountLock: AccountLock,
    private val updateAccountStatePort: UpdateAccountStatePort,
) : SendMoneyUseCase {
    override fun sendMoney(command: SendMoneyCommand): Boolean {
        val baselineDate = LocalDateTime.now().minusDays(10)
        val sourceAccount = loadAccountPort.loadAccount(
            accountId = command.sourceAccountId,
            baselineDate = baselineDate,
        )
        val targetAccount = loadAccountPort.loadAccount(
            accountId = command.targetAccountId,
            baselineDate = baselineDate,
        )

        val sourceAccountId = sourceAccount.id
        val targetAccountId = targetAccount.id

        accountLock.lockAccount(sourceAccountId)
        if (!sourceAccount.withdraw(command.money, targetAccountId)
        ) {
            accountLock.releaseAccount(sourceAccountId)
            return false
        }
        accountLock.lockAccount(targetAccountId)
        if (!targetAccount.deposit(command.money, sourceAccountId)) {
            accountLock.releaseAccount(sourceAccountId)
            accountLock.releaseAccount(targetAccountId)
            return false
        }

        updateAccountStatePort.updateActivities(sourceAccount)
        updateAccountStatePort.updateActivities(targetAccount)

        accountLock.releaseAccount(sourceAccountId)
        accountLock.releaseAccount(targetAccountId)
        return true
    }

    /* TODO: 추후 사용
    private fun checkThreshold(command: SendMoneyCommand) {
        if (command.money.isGreaterThan(moneyTransferProperties.maximumTransferThreshold)) {
            throw ThresholdExceededException(moneyTransferProperties.maximumTransferThreshold, command.money);
        }
    }*/
}