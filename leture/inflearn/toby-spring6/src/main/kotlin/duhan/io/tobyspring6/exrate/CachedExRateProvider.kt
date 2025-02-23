package duhan.io.tobyspring6.exrate

import duhan.io.tobyspring6.payment.ExRateProvider
import java.math.BigDecimal
import java.time.LocalDateTime

class CachedExRateProvider(
    private val target: ExRateProvider,
) : ExRateProvider {
    private var cachedExRate: BigDecimal? = null
    private var cacheExpiryTime: LocalDateTime? = null

    override fun getExRate(currency: String): BigDecimal {
        val now = LocalDateTime.now()
        if (cachedExRate == null || cacheExpiryTime?.isBefore(now) == true) {
            cachedExRate = target.getExRate(currency)
            cacheExpiryTime = now.plusSeconds(3)

            println("Cache Updated")
        }
        return cachedExRate!!
    }
}
