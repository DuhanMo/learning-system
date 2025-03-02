package duhan.io.project.account.application.port.out

import duhan.io.project.account.domain.Account.AccountId

interface AccountLock {
    fun lockAccount(accountId: AccountId)

    fun releaseAccount(accountId: AccountId)
}