package duhan.io.tobyspring6

import duhan.io.tobyspring6.payment.PaymentService
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

fun main() {
    val beanFactory = AnnotationConfigApplicationContext(ObjectFactory::class.java)
    val paymentService = beanFactory.getBean(PaymentService::class.java)

    val payment1 = paymentService.prepare(100L, "USD", BigDecimal("50.7"))
    println("payment1 = $payment1")
    println("-----------------------------------------")

    TimeUnit.SECONDS.sleep(1)

    val payment2 = paymentService.prepare(100L, "USD", BigDecimal("50.7"))
    println("payment2 = $payment2")
    println("-----------------------------------------")

    TimeUnit.SECONDS.sleep(2)

    val payment3 = paymentService.prepare(100L, "USD", BigDecimal("50.7"))
    println("payment3 = $payment3")
    println("-----------------------------------------")
}
