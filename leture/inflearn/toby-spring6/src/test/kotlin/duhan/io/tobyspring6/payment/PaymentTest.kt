package duhan.io.tobyspring6.payment

import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.jupiter.api.Test

class PaymentTest {
    @Test
    fun createPrepared() {
        val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val payment = Payment.createPrepared(
            orderId = 1L,
            currency = "USD",
            foreignCurrencyAmount = BigDecimal.TEN,
            exRate = BigDecimal("1000"),
            clock = clock,
        )

        payment.convertedAmount shouldBeEqualComparingTo BigDecimal("10000")
        payment.validUntil shouldBe LocalDateTime.now(clock).plusMinutes(30)
    }

    @Test
    fun isValid() {
        val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
        val payment = Payment.createPrepared(
            orderId = 1L,
            currency = "USD",
            foreignCurrencyAmount = BigDecimal.TEN,
            exRate = BigDecimal("1000"),
            clock = clock,
        )
        val offsetAddedClock = Clock.offset(clock, Duration.ofMinutes(30))
        payment.isValid(clock) shouldBe true
        payment.isValid(offsetAddedClock) shouldBe false
    }
}