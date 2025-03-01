package duhan.io.project.account.application.service

import duhan.io.project.account.application.port.out.AccountLock
import duhan.io.project.account.domain.Account
import org.springframework.stereotype.Component

@Component
class NoOpsAccountLock : AccountLock {
    override fun lockAccount(accountId: Account.AccountId) {
        // do nothing
    }

    override fun releaseAccount(accountId: Account.AccountId) {
        // do nothing
    }
}