package duhan.io.project.account.application.domain.service

import duhan.io.project.account.application.domain.model.Money


class ThresholdExceededException(threshold: Money, actual: Money) : RuntimeException(
    String.format("Maximum threshold for transferring money exceeded: tried to transfer $actual but threshold is $threshold!")
)