package duhan.io.tobyspring6.exrate

import duhan.io.tobyspring6.payment.ExRateProvider
import java.math.BigDecimal
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

class RestTemplateExRateProvider(
    private val restTemplate: RestTemplate,
) : ExRateProvider {
    override fun getExRate(currency: String): BigDecimal {
        val url = "https://open.er-api.com/v6/latest/$currency"
        return restTemplate.getForObject<ExRateData>(url).rates["KRW"] ?: BigDecimal.ZERO
    }
}