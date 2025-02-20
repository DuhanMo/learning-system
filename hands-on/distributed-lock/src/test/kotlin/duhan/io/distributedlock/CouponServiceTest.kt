package duhan.io.distributedlock

import duhan.io.distributedlock.config.AbstractTestContainerBase
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class CouponServiceTest @Autowired constructor(
    private val couponService: CouponService,
    private val couponRepository: CouponRepository,
) : AbstractTestContainerBase() {
    @Test
    fun `동시에 쿠폰을 여러 사람이 발급 시도하기 - 락 없이`() {
        val threadCount = 100
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)

        val coupon = couponRepository.save(Coupon(name = "쿠폰", stock = 100))
        for (i in 1..threadCount) {
            executor.submit {
                try {
                    val result = couponService.issueWithoutLock(coupon.id)
                    println("Thread-$i: issue result -> $result")
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()
        val updatedCoupon = couponRepository.findById(coupon.id).get()
        println("coupon.stock: ${updatedCoupon.stock}")
        assertThat(updatedCoupon.stock).isNotZero()
    }

    @Test
    fun `동시에 쿠폰을 여러 사람이 발급 시도하기 - 락 걸고`() {
        val threadCount = 100
        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)

        val coupon = couponRepository.save(Coupon(name = "쿠폰", stock = 100))
        for (i in 1..threadCount) {
            executor.submit {
                try {
                    val result = couponService.issueWithLock(coupon.id)
                    println("Thread-$i: issue result -> $result")
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()
        val updatedCoupon = couponRepository.findById(coupon.id).get()
        println("coupon.stock: ${updatedCoupon.stock}")
        assertThat(updatedCoupon.stock).isZero()
    }
}