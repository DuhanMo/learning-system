package duhan.io.tobyspring6.payment

import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PaymentServiceTest {
    private lateinit var clock: Clock

    @BeforeEach
    fun beforeEach() {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
    }

    @Test
    fun convertedAmount() {
        testAmount(exRate = BigDecimal("500"), convertedAmount = BigDecimal("5000"), clock = clock)
        testAmount(exRate = BigDecimal("1000"), convertedAmount = BigDecimal("10000"), clock = clock)
        testAmount(exRate = BigDecimal("3000"), convertedAmount = BigDecimal("30000"), clock = clock)
    }

    @Test
    fun validUntil() {
        val paymentService = PaymentService(ExRateProviderStub(BigDecimal("1000")), clock)
        val payment = paymentService.prepare(orderId = 1L, currency = "USD", foreignCurrencyAmount = BigDecimal.TEN)

        // valid_until 이 prepare() 30분 뒤로 설정되었는가?
        val now = LocalDateTime.now(clock)
        val expectedValidUntil = now.plusMinutes(30)

        payment.validUntil shouldBe expectedValidUntil
    }

    private fun testAmount(exRate: BigDecimal, convertedAmount: BigDecimal, clock: Clock) {
        val paymentService = PaymentService(ExRateProviderStub(exRate), clock)
        val payment = paymentService.prepare(orderId = 1L, currency = "USD", foreignCurrencyAmount = BigDecimal.TEN)

        payment.exRate shouldBeEqualComparingTo exRate
        payment.convertedAmount shouldBeEqualComparingTo convertedAmount
    }
}