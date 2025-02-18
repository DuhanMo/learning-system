package duhan.io.distributedlock

import java.util.concurrent.TimeUnit
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.*
import org.springframework.stereotype.Component

@Component
class SimpleLocker(
    private val redissonClient: RedissonClient,
) {
    private val logger = getLogger(SimpleLocker::class.java)

    fun <T> withLock(
        lockKey: String,
        waitTime: Long = 5L,
        leaseTime: Long = 1L,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        action: () -> T,
        onFailure: (() -> T)? = null,
    ): T? {
        val lock: RLock = redissonClient.getLock(lockKey)
        return try {
            if (lock.tryLock(waitTime, leaseTime, timeUnit)) {
                try {
                    return action()
                } finally {
                    try {
                        if (lock.isHeldByCurrentThread) {
                            lock.unlock()
                        }
                    } catch (e: Exception) {
                        logger.error("Failed to release lock: {}", lockKey, e)

                    }
                }
            } else {
                logger.warn("Failed to acquire lock for key: {}", lockKey)
                onFailure?.invoke()
            }
        } catch (e: Exception) {
            logger.error("Error while executing locked action for key: {}", lockKey, e)
            null
        }
    }
}