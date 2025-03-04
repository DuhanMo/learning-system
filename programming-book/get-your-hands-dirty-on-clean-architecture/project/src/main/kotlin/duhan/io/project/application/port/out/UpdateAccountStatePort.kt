package duhan.io.project.application.port.out

import duhan.io.project.application.domain.model.Account

interface UpdateAccountStatePort {
    fun updateActivities(account: Account)
}