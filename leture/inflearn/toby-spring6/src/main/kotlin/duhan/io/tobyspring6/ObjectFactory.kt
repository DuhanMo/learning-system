package duhan.io.tobyspring6

import duhan.io.tobyspring6.exrate.CachedExRateProvider
import duhan.io.tobyspring6.exrate.WebApiExRateProvider
import duhan.io.tobyspring6.payment.ExRateProvider
import duhan.io.tobyspring6.payment.PaymentService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectFactory {
    @Bean
    fun paymentService(): PaymentService = PaymentService(cachedExRateProvider())

    @Bean
    fun cachedExRateProvider(): ExRateProvider = CachedExRateProvider(exRateProvider())

    @Bean
    fun exRateProvider(): ExRateProvider = WebApiExRateProvider()
}
