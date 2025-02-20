package duhan.io.distributedlock

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("local")
@Configuration
class DataConfig(
    private val repository: CouponRepository,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        if (repository.findAll().isEmpty()) {
            saveDummy()
        }
    }

    private fun saveDummy() {
        val coupons = listOf(
            Coupon(
                name = "락 없이 발행하는 쿠폰",
                stock = 100,
            ),
            Coupon(
                name = "락 걸고 발행하는 쿠폰",
                stock = 100,
            )
        )
        repository.saveAll(coupons)
    }
}