package duhan.io.tobyspring6

import duhan.io.tobyspring6.payment.PaymentService
import java.math.BigDecimal
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main() {
    val beanFactory = AnnotationConfigApplicationContext(PaymentConfig::class.java)
    val paymentService = beanFactory.getBean(PaymentService::class.java)

    val payment = paymentService.prepare(100L, "USD", BigDecimal("50.7"))
    println("payment = $payment")
}
