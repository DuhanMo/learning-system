package ch01

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
    val calculator = CalculatorFactory.createPerformanceCalculator(aPerformance, play)
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

