package duhan.io.project.application.port.out

import duhan.io.project.application.domain.model.Account.AccountId


interface AccountLock {
    fun lockAccount(accountId: AccountId)

    fun releaseAccount(accountId: AccountId)
}