package duhan.io.distributedlock

import java.util.concurrent.TimeUnit

interface Locker {
    fun <T> withLock(
        lockKey: String,
        waitTime: Long = 5L,
        leaseTime: Long = 1L,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        onFailure: (() -> T)? = null,
        action: () -> T,
    ): T?
}
