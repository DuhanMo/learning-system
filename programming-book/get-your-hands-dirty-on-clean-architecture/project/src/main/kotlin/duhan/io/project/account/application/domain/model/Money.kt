package duhan.io.project.account.application.domain.model

import java.math.BigDecimal

data class Money(
    val amount: BigDecimal,
) {
    fun isPositiveOrZero(): Boolean = amount >= BigDecimal.ZERO

    fun isNegative(): Boolean = amount < BigDecimal.ZERO

    fun isPositive(): Boolean = amount > BigDecimal.ZERO

    fun isGreaterThanOrEqualTo(money: Money): Boolean = amount >= money.amount

    fun isGreaterThan(money: Money): Boolean = amount > money.amount

    operator fun minus(money: Money): Money = Money(amount.subtract(money.amount))

    operator fun plus(money: Money): Money = Money(amount.add(money.amount))

    fun negate(): Money = Money(amount.negate())

    companion object {
        val ZERO = of(0L)

        fun of(value: Long): Money {
            return Money(BigDecimal.valueOf(value))
        }

        fun add(a: Money, b: Money): Money {
            return Money(a.amount.add(b.amount))
        }

        fun subtract(a: Money, b: Money): Money {
            return Money(a.amount.subtract(b.amount))
        }
    }
}