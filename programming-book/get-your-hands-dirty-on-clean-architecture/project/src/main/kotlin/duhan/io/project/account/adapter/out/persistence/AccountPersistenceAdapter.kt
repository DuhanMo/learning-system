package duhan.io.project.account.adapter.out.persistence

import duhan.io.project.account.application.port.out.LoadAccountPort
import duhan.io.project.account.application.port.out.UpdateAccountStatePort
import duhan.io.project.account.application.domain.model.Account
import duhan.io.project.account.application.domain.model.Account.AccountId
import jakarta.persistence.EntityNotFoundException
import java.time.LocalDateTime
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class AccountPersistenceAdapter(
    private val accountRepository: SpringDataAccountRepository,
    private val activityRepository: ActivityRepository,
    private val accountMapper: AccountMapper,
) : LoadAccountPort, UpdateAccountStatePort {
    override fun loadAccount(accountId: AccountId, baselineDate: LocalDateTime): Account {
        val account = accountRepository.findByIdOrNull(accountId.value) ?: throw EntityNotFoundException()
        val activities = activityRepository.findByOwnerSince(
            ownerAccountId = accountId.value,
            since = baselineDate,
        )
        val withdrawalBalance = activityRepository.getWithdrawalBalanceUntil(
            accountId = accountId.value,
            until = baselineDate,
        )

        val depositBalance = activityRepository.getDepositBalanceUntil(
            accountId = accountId.value,
            until = baselineDate,
        )

        return accountMapper.mapToDomainEntity(
            account = account,
            activities = activities,
            withdrawalBalance = withdrawalBalance,
            depositBalance = depositBalance,
        )
    }

    override fun updateActivities(account: Account) {
        account.activityWindow.activities.forEach { activity ->
            if (activity.id == null) {
                activityRepository.save(accountMapper.mapToJpaEntity(activity))
            }
        }
    }
}