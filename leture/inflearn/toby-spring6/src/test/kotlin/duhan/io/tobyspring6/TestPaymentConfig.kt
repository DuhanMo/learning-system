package duhan.io.tobyspring6

import duhan.io.tobyspring6.payment.ExRateProvider
import duhan.io.tobyspring6.payment.ExRateProviderStub
import duhan.io.tobyspring6.payment.PaymentService
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestPaymentConfig {
    @Bean
    fun paymentService(): PaymentService = PaymentService(exRateProvider(), clock())

    @Bean
    fun exRateProvider(): ExRateProvider = ExRateProviderStub(BigDecimal("1000"))

    @Bean
    fun clock(): Clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())

}