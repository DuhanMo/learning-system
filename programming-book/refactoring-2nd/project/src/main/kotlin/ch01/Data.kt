package ch01

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

data class StatementData(
    val customer: String,
    val performances: List<EnrichedPerformance>,
    val totalAmount: Int,
    val totalVolumeCredits: Int,
)

data class EnrichedPerformance(
    val playID: String,
    val audience: Int,
    val play: Play,
    val amount: Int,
    val volumeCredits: Int,
)

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
