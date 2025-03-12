package ch01

object CalculatorFactory {
    fun createPerformanceCalculator(performance: Performance, play: Play): PerformanceCalculator =
        when (play.type) {
            "tragedy" -> TragedyCalculator(performance, play)
            "comedy" -> ComedyCalculator(performance, play)
            else -> throw IllegalArgumentException("Unknown play type: ${play.type}")
        }
}