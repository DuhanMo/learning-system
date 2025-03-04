package duhan.io.project.adapter.out.persistence

import duhan.io.project.application.domain.model.Account.AccountId
import duhan.io.project.application.port.out.AccountLock
import org.springframework.stereotype.Component

@Component
class NoOpAccountLock : AccountLock {
    override fun lockAccount(accountId: AccountId) {
        // do nothing
    }

    override fun releaseAccount(accountId: AccountId) {
        // do nothing
    }
}
