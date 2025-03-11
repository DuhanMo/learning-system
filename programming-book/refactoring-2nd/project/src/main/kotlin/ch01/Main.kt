package ch01

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max


fun statement(invoice: Invoice): String {
    val statementData = createStatementData(invoice)
    return renderPainText(statementData)
}

fun createStatementData(invoice: Invoice): StatementData {
    val performances = invoice.performances.map { enrichPerformance(it) }
    return StatementData(
        customer = invoice.customer,
        performances = performances,
        totalAmount = totalAmount(performances),
        totalVolumeCredits = totalVolumeCredits(performances),
    )
}

fun enrichPerformance(aPerformance: Performance): EnrichedPerformance {
    val play = playFor(aPerformance)
    return EnrichedPerformance(
        playID = aPerformance.playID,
        audience = aPerformance.audience,
        play = play,
        amount = amountFor(aPerformance, play),
        volumeCredits = volumeCreditsFor(aPerformance, play),
    )
}

fun playFor(aPerformance: Performance): Play {
    return plays[aPerformance.playID]!!
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

fun volumeCreditsFor(aPerformance: Performance, play: Play): Int {
    var result = 0
    result += max(aPerformance.audience - 30, 0)
    // 희극 관객 5명마다 추가 포인트를 제공한다.
    if ("comedy" == play.type) {
        result += (aPerformance.audience / 5)
    }
    return result
}

fun usd(aNumber: Number): String = NumberFormat.getCurrencyInstance(Locale.US).apply {
    minimumFractionDigits = 2
}.format(aNumber.toDouble() / 100)

fun totalAmount(performances: List<EnrichedPerformance>): Int {
    var result = 0
    for (perf in performances) {
        result += perf.amount
    }
    return result
}

fun totalVolumeCredits(performances: List<EnrichedPerformance>): Int {
    var result = 0
    for (perf in performances) {
        result += perf.volumeCredits
    }
    return result
}

fun renderPainText(data: StatementData): String {
    var result = "청구 내역 (고객명: ${data.customer})\n"
    for (perf in data.performances) {
        // 청구 내역을 출력한다.
        result += " ${perf.play.name}: ${usd(perf.amount)} (${perf.audience}석)\n"
    }
    result += "총액: ${usd(data.totalAmount)}\n"
    result += "적립 포인트: ${data.totalVolumeCredits}점\n"
    return result
}

fun main() {
    val result = statement(invoices[0])
    println(result)
}
