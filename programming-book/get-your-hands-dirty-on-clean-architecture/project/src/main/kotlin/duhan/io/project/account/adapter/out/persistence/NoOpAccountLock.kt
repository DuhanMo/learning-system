package duhan.io.project.account.adapter.out.persistence

import duhan.io.project.account.application.domain.model.Account
import duhan.io.project.account.application.port.out.AccountLock
import org.springframework.stereotype.Component

@Component
class NoOpAccountLock: AccountLock {
    override fun lockAccount(accountId: Account.AccountId) {
        // do nothing
    }

    override fun releaseAccount(accountId: Account.AccountId) {
        // do nothing
    }
}
