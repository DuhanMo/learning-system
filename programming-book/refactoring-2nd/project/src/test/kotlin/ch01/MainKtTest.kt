package ch01

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainKtTest {
    @Test
    fun statementTest() {
        val expected = "청구 내역 (고객명: BigCo)\n" +
                " hamlet: ${'$'}650.00 (55석)\n" +
                " As You Like It: ${'$'}580.00 (35석)\n" +
                " Othello: ${'$'}500.00 (40석)\n" +
                "총액: ${'$'}1,730.00\n" +
                "적립 포인트: 47점\n"
        assertEquals(expected, statement(invoices[0], plays))
    }
}