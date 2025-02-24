package duhan.io.tobyspring6.payment

import java.math.BigDecimal

class ExRateProviderStub(var exRate: BigDecimal) : ExRateProvider {
    override fun getExRate(currency: String): BigDecimal = exRate
}