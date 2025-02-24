package duhan.io.tobyspring6.payment

import duhan.io.tobyspring6.TestPaymentConfig
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import java.io.IOException
import java.math.BigDecimal
import java.time.Clock
import java.time.LocalDateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestPaymentConfig::class])
class PaymentServiceSpringTest(
    @Autowired private val paymentService: PaymentService,
    @Autowired private val exRateProviderStub: ExRateProviderStub,
    @Autowired private val clock: Clock,
) {
    @Test
    @Throws(IOException::class)
    fun convertedAmount() {
        // exRate: 1000
        val payment1 = paymentService.prepare(1L, "USD", BigDecimal.TEN)

        payment1.exRate shouldBeEqualComparingTo BigDecimal("1000")
        payment1.convertedAmount shouldBeEqualComparingTo BigDecimal("10000")

        // exRate: 500
        exRateProviderStub.exRate = BigDecimal("500")
        val payment2 = paymentService.prepare(1L, "USD", BigDecimal.TEN)

        payment2.exRate shouldBeEqualComparingTo BigDecimal("500")
        payment2.convertedAmount shouldBeEqualComparingTo BigDecimal("5000")
    }

    @Test
    fun validUntil() {
        val payment = paymentService.prepare(1L, "USD", BigDecimal.TEN)

        // valid until이 prepare() 30분 뒤로 설정됐는가?
        val now = LocalDateTime.now(this.clock)
        val expectedValidUntil = now.plusMinutes(30)

        payment.validUntil shouldBe expectedValidUntil
    }
}