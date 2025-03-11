package ch01

import java.text.NumberFormat
import java.util.Locale


fun statement(invoice: Invoice): String {
    return renderPainText(createStatementData(invoice))
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

fun usd(aNumber: Number): String = NumberFormat.getCurrencyInstance(Locale.US).apply {
    minimumFractionDigits = 2
}.format(aNumber.toDouble() / 100)

fun main() {
    val result = statement(invoices[0])
    println(result)
}
