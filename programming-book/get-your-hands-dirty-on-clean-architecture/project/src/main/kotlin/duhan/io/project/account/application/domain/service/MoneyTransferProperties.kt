package duhan.io.project.account.application.domain.service

import duhan.io.project.account.application.domain.model.Money

data class MoneyTransferProperties(val maximumTransferThreshold: Money = Money.of(1_000_000L))