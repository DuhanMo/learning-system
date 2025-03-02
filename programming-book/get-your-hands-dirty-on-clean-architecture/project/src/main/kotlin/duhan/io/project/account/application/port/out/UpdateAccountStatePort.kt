package duhan.io.project.account.application.port.out

import duhan.io.project.account.application.domain.model.Account

interface UpdateAccountStatePort {
    fun updateActivities(account: Account)
}