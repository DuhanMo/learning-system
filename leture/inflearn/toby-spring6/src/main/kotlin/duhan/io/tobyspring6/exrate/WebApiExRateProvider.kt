package duhan.io.tobyspring6.exrate

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import duhan.io.tobyspring6.payment.ExRateProvider
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors

class WebApiExRateProvider : ExRateProvider {
    override fun getExRate(currency: String): BigDecimal {
        val url = URL("https://open.er-api.com/v6/latest/$currency")
        val connection = url.openConnection() as HttpURLConnection
        val br = BufferedReader(InputStreamReader(connection.inputStream))
        val response = br.lines().collect(Collectors.joining())
        br.close()

        val mapper = ObjectMapper()
        val data = mapper.readValue<ExRateData>(response)
        println("API ExRate: ${data.rates["KRW"]!!}")
        return data.rates["KRW"]!!
    }
}
