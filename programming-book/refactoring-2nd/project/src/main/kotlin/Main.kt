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
    var totalAmount = 0
    var volumeCredits = 0
    var result = "청구 내역 (고객명: ${invoice.customer})\n"

    fun format(amount: Number): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        formatter.minimumFractionDigits = 2
        return formatter.format(amount.toDouble())
    }

    for (perf in invoice.performances) {
        volumeCredits += volumeCreditsFor(perf)

        // 청구 내역을 출력한다.
        result += " ${playFor(perf).name}: ${format(amountFor(perf) / 100)} (${perf.audience}석)\n"
        totalAmount += amountFor(perf)
    }
    result += "총액: ${format(totalAmount / 100)}\n"
    result += "적립 포인트: ${volumeCredits}점\n"
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

fun volumeCreditsFor(perf: Performance): Int {
    var volumeCredits = 0
    volumeCredits += max(perf.audience - 30, 0)
    // 희극 관객 5명마다 추가 포인트를 제공한다.
    if ("comedy" == playFor(perf).type) {
        volumeCredits += (perf.audience / 5)
    }
    return volumeCredits
}

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