package duhan.io.project.account.application.port.out

import duhan.io.project.account.domain.Account

interface UpdateAccountStatePort {
    fun updateActivities(account: Account)
}