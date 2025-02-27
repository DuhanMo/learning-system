package duhan.io.project.account.domain

import java.math.BigInteger

data class Money private constructor(
    val amount: BigInteger,
) {
    fun isPositiveOrZero(): Boolean = amount >= BigInteger.ZERO

    fun isNegative(): Boolean = amount < BigInteger.ZERO

    fun isPositive(): Boolean = amount > BigInteger.ZERO

    fun isGreaterThanOrEqualTo(money: Money): Boolean = amount >= money.amount

    fun isGreaterThan(money: Money): Boolean = amount > money.amount

    operator fun minus(money: Money): Money = Money(amount.subtract(money.amount))

    operator fun plus(money: Money): Money = Money(amount.add(money.amount))

    fun negate(): Money = Money(amount.negate())

    companion object {
        val ZERO = of(0L)

        fun of(value: Long): Money {
            return Money(BigInteger.valueOf(value))
        }

        fun add(a: Money, b: Money): Money {
            return Money(a.amount.add(b.amount))
        }

        fun subtract(a: Money, b: Money): Money {
            return Money(a.amount.subtract(b.amount))
        }
    }
}