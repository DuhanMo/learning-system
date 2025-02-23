package duhan.io.tobyspring6.payment

import java.math.BigDecimal
import java.time.LocalDateTime

data class Payment(
    val orderId: Long,
    val currency: String,
    val foreignCurrencyAmount: BigDecimal,
    val exRate: BigDecimal,
    val convertedAmount: BigDecimal = foreignCurrencyAmount.multiply(exRate),
    val validUntil: LocalDateTime = LocalDateTime.now().plusMinutes(30),
)
