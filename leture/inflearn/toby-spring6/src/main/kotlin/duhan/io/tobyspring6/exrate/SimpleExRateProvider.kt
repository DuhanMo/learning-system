package duhan.io.tobyspring6.exrate

import duhan.io.tobyspring6.payment.ExRateProvider
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class SimpleExRateProvider : ExRateProvider {
    override fun getExRate(currency: String): BigDecimal =
        when (currency) {
            "USD" -> BigDecimal("1000")
            else -> throw IllegalArgumentException("지원되지 않는 통화입니다")
        }
}
