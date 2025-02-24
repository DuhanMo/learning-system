package duhan.io.tobyspring6.payment

import java.math.BigDecimal
import java.time.Clock

class PaymentService(
    private val exRateProvider: ExRateProvider,
    private val clock: Clock,
) {
    fun prepare(
        orderId: Long,
        currency: String,
        foreignCurrencyAmount: BigDecimal,
    ): Payment =
        Payment.createPrepared(
            orderId = orderId,
            currency = currency,
            foreignCurrencyAmount = foreignCurrencyAmount,
            exRate = exRateProvider.getExRate(currency),
            clock = clock,
        )
}
