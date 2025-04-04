package duhan.io.tobyspring6

import duhan.io.tobyspring6.exrate.RestTemplateExRateProvider
import duhan.io.tobyspring6.exrate.WebApiExRateProvider
import duhan.io.tobyspring6.payment.ExRateProvider
import duhan.io.tobyspring6.payment.PaymentService
import java.time.Clock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class PaymentConfig {
    @Bean
    fun paymentService(): PaymentService = PaymentService(exRateProvider(), clock())

    @Bean
    fun restTemplate(): RestTemplate = RestTemplate(JdkClientHttpRequestFactory())

    @Bean
    fun exRateProvider(): ExRateProvider = RestTemplateExRateProvider(restTemplate())

    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()
}
