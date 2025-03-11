package ch01

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainKtTest {
    @Test
    fun statementTest() {
        val expected = "청구 내역 (고객명: BigCo)\n" +
                " hamlet: $650.00 (55석)\n" +
                " As You Like It: $580.00 (35석)\n" +
                " Othello: $500.00 (40석)\n" +
                "총액: $1,730.00\n" +
                "적립 포인트: 47점\n"
        assertEquals(expected, statement(invoices[0]))
    }

    @Test
    fun htmlStatementTest() {
        val expected = """
            <h1>청구 내역 (고객명: BigCo)</h1>
            <table>
            <tr><th>연극</th><th>좌석 수</th><th>금액</th></tr> <tr><td>hamlet</td><td>(55석)</td> <td>$650.00</td></tr>
             <tr><td>As You Like It</td><td>(35석)</td> <td>$580.00</td></tr>
             <tr><td>Othello</td><td>(40석)</td> <td>$500.00</td></tr>
            </table>
            <p>총액: <em>$1,730.00</em></p>
            <p>적립 포인트: <em>47</em>점</p>
        """.trimIndent()
        assertEquals(expected, htmlStatement(invoices[0]).trimIndent())
    }
}