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
        val play = plays[perf.playID]!!
        val thisAmount = amountFor(perf, play)
        // 포인트를 적립한다.
        volumeCredits += max(perf.audience - 30, 0)
        // 희극 관객 5명마다 추가 포인트를 제공한다.
        if ("comedy" == play.type) volumeCredits += (perf.audience / 5)
        // 청구 내역을 출력한다.
        result += " ${play.name}: ${format(thisAmount / 100)} (${perf.audience}석)\n"
        totalAmount += thisAmount
    }
    result += "총액: ${format(totalAmount / 100)}\n"
    result += "적립 포인트: ${volumeCredits}점\n"
    return result
}

fun amountFor(aPerformance: Performance, play: Play): Int {
    var result: Int

    when (play.type) {
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

        else -> throw IllegalArgumentException("알 수 없는 장르: ${play.type}")
    }

    return result
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