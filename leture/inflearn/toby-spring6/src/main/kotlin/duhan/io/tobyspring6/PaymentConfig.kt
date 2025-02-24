package duhan.io.tobyspring6

import duhan.io.tobyspring6.exrate.WebApiExRateProvider
import duhan.io.tobyspring6.payment.ExRateProvider
import duhan.io.tobyspring6.payment.PaymentService
import java.time.Clock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PaymentConfig {
    @Bean
    fun paymentService(): PaymentService = PaymentService(exRateProvider(), clock())

    @Bean
    fun exRateProvider(): ExRateProvider = WebApiExRateProvider()

    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()
}
