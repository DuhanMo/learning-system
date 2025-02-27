package duhan.io.project.account.application.service

import duhan.io.project.account.domain.Money

class ThresholdExceededException(threshold: Money, actual: Money) : RuntimeException(
    String.format("Maximum threshold for transferring money exceeded: tried to transfer $actual but threshold is $threshold!")
)