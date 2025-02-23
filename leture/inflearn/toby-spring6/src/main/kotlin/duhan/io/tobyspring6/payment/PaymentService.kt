package duhan.io.tobyspring6.payment

import java.math.BigDecimal

class PaymentService(private val exRateProvider: ExRateProvider) {
    fun prepare(
        orderId: Long,
        currency: String,
        foreignCurrencyAmount: BigDecimal,
    ): Payment =
        Payment(
            orderId = orderId,
            currency = currency,
            foreignCurrencyAmount = foreignCurrencyAmount,
            exRate = exRateProvider.getExRate(currency),
        )
}
