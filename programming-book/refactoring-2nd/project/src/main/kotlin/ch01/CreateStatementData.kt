package ch01

import kotlin.math.max

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
    val calculator = PerformanceCalculator(aPerformance, play)
    return EnrichedPerformance(
        playID = aPerformance.playID,
        audience = aPerformance.audience,
        play = calculator.play,
        amount = calculator.amount(),
        volumeCredits = calculator.volumeCredits(),
    )
}

fun playFor(aPerformance: Performance): Play {
    return plays[aPerformance.playID]!!
}

fun totalAmount(performances: List<EnrichedPerformance>): Int = performances.sumOf { it.amount }

fun totalVolumeCredits(performances: List<EnrichedPerformance>): Int = performances.sumOf { it.volumeCredits }

class PerformanceCalculator(
    val performance: Performance,
    val play: Play,
) {
    fun amount(): Int {
        var result: Int

        when (play.type) {
            "tragedy" -> { // 비극
                result = 40_000
                if (performance.audience > 30) {
                    result += 1_000 * (performance.audience - 30)
                }
            }

            "comedy" -> { // 희극
                result = 30_000
                if (performance.audience > 20) {
                    result += 10_000 + 500 * (performance.audience - 20)
                }
                result += 300 * performance.audience
            }

            else -> throw IllegalArgumentException("알 수 없는 장르: ${play.type}")
        }

        return result
    }

    fun volumeCredits(): Int {
        var result = 0
        result += max(performance.audience - 30, 0)
        // 희극 관객 5명마다 추가 포인트를 제공한다.
        if ("comedy" == play.type) {
            result += (performance.audience / 5)
        }
        return result
    }
}