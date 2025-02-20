package duhan.io.distributedlock

import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CouponService(
    private val locker: Locker,
    private val repository: CouponRepository,
) {
    private val logger = getLogger(CouponService::class.java)

    fun issueWithoutLock(id: Long) {
        val coupon = repository.findByIdOrNull(id) ?: throw NoSuchElementException()
        if (coupon.stock > 0) {
            coupon.stock -= 1
            repository.save(coupon)
            logger.info("Issued coupon: $id, remaining stock: ${coupon.stock}")
        } else {
            logger.warn("Coupon $id is out of stock")
        }
    }

    fun issueWithLock(id: Long) {
        locker.withLock(lockKey = "coupon::$id") {
            val coupon = repository.findByIdOrNull(id) ?: throw NoSuchElementException()
            if (coupon.stock > 0) {
                coupon.stock -= 1
                repository.save(coupon)
                logger.info("Issued coupon: $id, remaining stock: ${coupon.stock}")
            } else {
                logger.warn("Coupon $id is out of stock")
            }
        }
    }
}