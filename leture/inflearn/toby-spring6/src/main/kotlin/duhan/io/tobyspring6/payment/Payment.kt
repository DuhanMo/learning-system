package duhan.io.tobyspring6.payment

import java.math.BigDecimal
import java.time.Clock
import java.time.LocalDateTime

data class Payment(
    val orderId: Long,
    val currency: String,
    val foreignCurrencyAmount: BigDecimal,
    val exRate: BigDecimal,
    val convertedAmount: BigDecimal = foreignCurrencyAmount.multiply(exRate),
    val validUntil: LocalDateTime = LocalDateTime.now().plusMinutes(30),
) {
    fun isValid(clock: Clock): Boolean = LocalDateTime.now(clock).isBefore(validUntil)

    companion object {
        fun createPrepared(
            orderId: Long,
            currency: String,
            foreignCurrencyAmount: BigDecimal,
            exRate: BigDecimal,
            clock: Clock,
        ): Payment {
            val convertedAmount = foreignCurrencyAmount.multiply(exRate)
            val validUntil = LocalDateTime.now(clock).plusMinutes(30)
            return Payment(orderId, currency, foreignCurrencyAmount, exRate, convertedAmount, validUntil)
        }
    }
}
