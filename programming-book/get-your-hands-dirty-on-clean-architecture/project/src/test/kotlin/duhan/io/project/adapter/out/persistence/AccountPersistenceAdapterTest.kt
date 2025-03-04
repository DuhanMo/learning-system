package duhan.io.project.adapter.out.persistence

import duhan.io.project.application.domain.model.Account.AccountId
import duhan.io.project.application.domain.model.ActivityWindow
import duhan.io.project.application.domain.model.Money
import duhan.io.project.fixtures.createAccount
import duhan.io.project.fixtures.createActivity
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql

@DataJpaTest
@Import(AccountPersistenceAdapter::class, AccountMapper::class)
class AccountPersistenceAdapterTest(
    @Autowired private val adapterUnderTest: AccountPersistenceAdapter,
    @Autowired private val activityRepository: ActivityRepository,
) {
    @Test
    @Sql("AccountPersistenceAdapterTest.sql")
    fun loadsAccount() {
        val account = adapterUnderTest.loadAccount(
            accountId = AccountId(1L),
            baselineDate = LocalDateTime.of(2018, 8, 10, 0, 0),
        )

        assertThat(account.activityWindow.activities).hasSize(2)
        assertThat(account.calculateBalance()).isEqualTo(Money.of(500L))
    }

    @Test
    fun updatesActivities() {
        val account = createAccount(
            id = AccountId(42L),
            baselineBalance = Money.of(555L),
            activityWindow = ActivityWindow(createActivity(id = null, money = Money.of(1L))),
        )

        adapterUnderTest.updateActivities(account)

        assertThat(activityRepository.count()).isEqualTo(1)

        val savedActivity = activityRepository.findAll().first()
        assertThat(savedActivity.amount).isEqualTo(1L)
    }
}
