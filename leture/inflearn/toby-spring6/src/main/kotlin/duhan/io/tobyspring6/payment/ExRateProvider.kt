package duhan.io.tobyspring6.payment

import java.math.BigDecimal

interface ExRateProvider {
    fun getExRate(currency: String): BigDecimal
}
