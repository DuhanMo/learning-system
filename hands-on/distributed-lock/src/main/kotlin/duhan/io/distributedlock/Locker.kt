package duhan.io.distributedlock

import java.util.concurrent.TimeUnit

interface Locker {
    fun <T> withLock(
        lockKey: String,
        waitTime: Long,
        leaseTime: Long = 1L,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        action: () -> T,
        onFailure: (() -> T)? = null
    ): T?
}
