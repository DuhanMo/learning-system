package duhan.io.tobyspring6

import duhan.io.tobyspring6.data.OrderRepository
import duhan.io.tobyspring6.order.Order
import java.math.BigDecimal
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.support.TransactionTemplate

class DataClient

fun main() {
    val beanFactory = AnnotationConfigApplicationContext(DataConfig::class.java)
    val repository = beanFactory.getBean(OrderRepository::class.java)
    val transactionManager = beanFactory.getBean(JpaTransactionManager::class.java)
    try {
        TransactionTemplate(transactionManager).execute {
            val order = Order(no = "100", total = BigDecimal.TEN)
            repository.save(order)

            println("order = ${order}")

            val order2 = Order(no = "100", total = BigDecimal.TEN)
            repository.save(order2)
        }
    } catch (e: DataIntegrityViolationException) {
        println("주문번호 중복 복구 작업")
    }
}