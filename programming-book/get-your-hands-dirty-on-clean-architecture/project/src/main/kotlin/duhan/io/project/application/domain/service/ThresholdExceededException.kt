package duhan.io.project.application.domain.service

import duhan.io.project.application.domain.model.Money

class ThresholdExceededException(threshold: Money, actual: Money) : RuntimeException(
    String.format("Maximum threshold for transferring money exceeded: tried to transfer $actual but threshold is $threshold!")
)