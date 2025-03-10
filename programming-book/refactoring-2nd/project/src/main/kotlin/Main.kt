package duhan.io

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

val plays = mapOf(
    "hamlet" to Play(name = "hamlet", type = "tragedy"),
    "as-like" to Play(name = "As You Like It", type = "comedy"),
    "othello" to Play(name = "Othello", type = "tragedy"),
)

val invoices = listOf(
    Invoice(
        customer = "BigCo",
        performances = listOf(
            Performance(
                playID = "hamlet",
                audience = 55,
            ),
            Performance(
                playID = "as-like",
                audience = 35
            ),
            Performance(
                playID = "othello",
                audience = 40
            )
        )
    )
)

fun statement(invoice: Invoice, plays: Map<String, Play>): String {
    fun totalVolumeCredits(): Int {
        var result = 0
        for (perf in invoice.performances) {
            result += volumeCreditsFor(perf)
        }
        return result
    }

    fun totalAmount(): Int {
        var result = 0
        for (perf in invoice.performances) {
            result += amountFor(perf)
        }
        return result
    }

    var result = "청구 내역 (고객명: ${invoice.customer})\n"
    for (perf in invoice.performances) {
        // 청구 내역을 출력한다.
        result += " ${playFor(perf).name}: ${usd(amountFor(perf))} (${perf.audience}석)\n"
    }
    result += "총액: ${usd(totalAmount())}\n"
    result += "적립 포인트: ${totalVolumeCredits()}점\n"
    return result
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

fun playFor(aPerformance: Performance): Play {
    return plays[aPerformance.playID]!!
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

fun main() {
    val result = statement(invoices[0], plays)
    println(result)
}


data class Play(
    val name: String,
    val type: String,
)

data class Invoice(
    val customer: String,
    val performances: List<Performance>
)

data class Performance(
    val playID: String,
    val audience: Int,
)