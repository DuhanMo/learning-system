package ch01

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max


fun statement(invoice: Invoice, plays: Map<String, Play>): String {
    val statementData = StatementData(invoice.customer, invoice.performances)
    return renderPainText(statementData, plays)
}

fun renderPainText(data: StatementData, plays: Map<String, Play>): String {
    fun playFor(aPerformance: Performance): Play {
        return plays[aPerformance.playID]!!
    }

    fun amountFor(aPerformance: Performance): Int {
        var result: Int

        when (playFor(aPerformance).type) {
            "tragedy" -> { // 비극
                result = 40_000
                if (aPerformance.audience > 30) {
                    result += 1_000 * (aPerformance.audience - 30)
                }
            }

            "comedy" -> { // 희극
                result = 30_000
                if (aPerformance.audience > 20) {
                    result += 10_000 + 500 * (aPerformance.audience - 20)
                }
                result += 300 * aPerformance.audience
            }

            else -> throw IllegalArgumentException("알 수 없는 장르: ${playFor(aPerformance).type}")
        }

        return result
    }

    fun volumeCreditsFor(aPerformance: Performance): Int {
        var result = 0
        result += max(aPerformance.audience - 30, 0)
        // 희극 관객 5명마다 추가 포인트를 제공한다.
        if ("comedy" == playFor(aPerformance).type) {
            result += (aPerformance.audience / 5)
        }
        return result
    }

    fun usd(aNumber: Number): String = NumberFormat.getCurrencyInstance(Locale.US).apply {
        minimumFractionDigits = 2
    }.format(aNumber.toDouble() / 100)

    fun totalVolumeCredits(): Int {
        var result = 0
        for (perf in data.performances) {
            result += volumeCreditsFor(perf)
        }
        return result
    }

    fun totalAmount(): Int {
        var result = 0
        for (perf in data.performances) {
            result += amountFor(perf)
        }
        return result
    }

    var result = "청구 내역 (고객명: ${data.customer})\n"
    for (perf in data.performances) {
        // 청구 내역을 출력한다.
        result += " ${playFor(perf).name}: ${usd(amountFor(perf))} (${perf.audience}석)\n"
    }
    result += "총액: ${usd(totalAmount())}\n"
    result += "적립 포인트: ${totalVolumeCredits()}점\n"
    return result
}

data class StatementData(
    val customer: String,
    val performances: List<Performance>,
)

fun main() {
    val result = statement(invoices[0], plays)
    println(result)
}
