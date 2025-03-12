package ch01

import kotlin.math.max

abstract class PerformanceCalculator(
    val play: Play,
) {
    abstract fun amount(): Int

    abstract fun volumeCredits(): Int
}

class TragedyCalculator(
    val performance: Performance,
    play: Play,
) : PerformanceCalculator(play) {
    override fun amount(): Int {
        var result: Int
        result = 40_000
        if (performance.audience > 30) {
            result += 1_000 * (performance.audience - 30)
        }
        return result
    }

    override fun volumeCredits(): Int = max(performance.audience - 30, 0)
}

class ComedyCalculator(
    val performance: Performance,
    play: Play,
) : PerformanceCalculator(play) {
    override fun amount(): Int {
        var result = 30_000
        if (performance.audience > 20) {
            result += 10_000 + 500 * (performance.audience - 20)
        }
        result += 300 * performance.audience
        return result
    }

    override fun volumeCredits(): Int = max(performance.audience - 30, 0) + (performance.audience / 5)
}
