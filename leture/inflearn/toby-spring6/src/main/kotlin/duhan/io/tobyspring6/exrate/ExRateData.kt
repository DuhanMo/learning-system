package duhan.io.tobyspring6.exrate

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
class ExRateData(
    val result: String = "",
    val rates: Map<String, BigDecimal> = mapOf(),
)
