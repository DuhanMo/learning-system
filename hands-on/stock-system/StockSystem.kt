import java.io.*;
import java.util.*;
import java.time.*;

data class Purchase(
    val code: String,
    val quantity: Int,
    val unitPrice: Int,
    val purchasedAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(quantity > 0) {"수량은 양수이어야 합니다."}
        require(unitPrice > 0) {"금액은 양수이어야 합니다."}
    }

    val totalPrice: Int =  quantity * unitPrice
}

class StockHolding(
    val purchasesMap: MutableMap<String, MutableList<Purchase>>  = mutableMapOf(),
){

    fun buy(code: String, quantity: Int, unitPrice: Int) {
        val purchases = purchasesMap.getOrPut(code) {mutableListOf()}
        purchases.add(Purchase(code, quantity, unitPrice))
    }
    fun sell(code: String, sellQuantity: Int) {
        val purchases = purchasesMap[code] ?: throw IllegalArgumentException("존재하지 않는 주식 코드입니다.")
        val totalQuantity = getTotalQuantity(code)
        require(sellQuantity <= totalQuantity) { "총 보유 수량보다 초과하여 매도할 수 없습니다." }

        var remainingSell = sellQuantity
        val iterator = purchases.iterator()
        while(iterator.hasNext() && remainingSell > 0) {
            val purchase = iterator.next()
            if (purchase.quantity <= remainingSell) {
                // 현재 매수분 전체 매도
                remainingSell -= purchase.quantity
                iterator.remove()
            } else {
                // 현재 매수분 부분 매도
                val partialSelledPurchase = Purchase(
                    code = code,
                    quantity = purchase.quantity - remainingSell,
                    unitPrice = purchase.unitPrice,
                    purchasedAt = purchase.purchasedAt,
                )
                iterator.remove()
                purchases.add(0,partialSelledPurchase)
                remainingSell = 0
            }
        }
        if (purchases.isEmpty()) {
            purchasesMap.remove(code)
        }
    }
    fun findAllCode(): Set<String>
            = purchasesMap.filter { (code, _) -> getTotalQuantity(code) > 0 }.keys


    fun getTotalQuantity(code: String): Int {
        val purchases = purchasesMap[code]?: return 0
        return purchases.sumOf {it.quantity}
    }


    fun getAveragePrice(code: String): Double {
        val purchases = purchasesMap[code]?: return 0.0
        return purchases.sumOf { it.totalPrice } / purchases.sumOf { it.quantity.toDouble() }
    }
}

class Portfolio(val holding: StockHolding) {
    val stockPrices: MutableMap<String, Int> = mutableMapOf()

    fun buy(code: String, quantity: Int, unitPrice: Int) {
        holding.buy(code, quantity, unitPrice)
    }

    fun sell(code: String, sellQuantity: Int) {
        holding.sell(code, sellQuantity)
    }

    fun findAllStocks(): List<StockInfo> {
        return holding.findAllCode()
            .map { code ->
                val totalQuantity = holding.getTotalQuantity(code)
                val avaeragePrice = holding.getAveragePrice(code)
                val currentStockPrice = stockPrices[code] ?: 0
                StockInfo(code, totalQuantity, avaeragePrice, currentStockPrice)
            }.filter { it.totalQuantity > 0 }
            .sortedWith(
                compareByDescending<StockInfo> {it.totalQuantity}
                    .thenBy { it.code }
            )
    }
}

data class StockInfo(
    val code: String,
    val totalQuantity: Int,
    val avaeragePrice: Double,
    val currentStockPrice: Int,
)

private fun printPortfolio(portfolio: Portfolio) {
    println("=========================")
    println("코드 : 총보유수량 : 평균매수가  :  현재 가격 ")
    portfolio.findAllStocks().forEach {
        println("${it.code} : ${it.totalQuantity}    :   ${it.avaeragePrice}   :   ${it.currentStockPrice}")
    }
    println("=========================")
}

fun main() {
    runTests()
    val portfolio = Portfolio(StockHolding())
    // 애플 주식 총 20주
    portfolio.buy("APPL", 10, 500)
    portfolio.buy("APPL", 10, 400)

    // 엔비디아 주식 총 10주
    portfolio.buy("NVDL", 5, 700)
    portfolio.buy("NVDL", 5, 700)

    // 애플 주식 5주 판매
    portfolio.sell("APPL", 5)
    printPortfolio(portfolio)

}

private fun runTests() {
    test("구매 주식의 구매 수량과 단위 가격은 0이하일 시 예외 발생한다") {
        assertThrows<IllegalArgumentException> {
            Purchase("APPL", -1, -1)
        }
    }

    test("보유 주식은 주식을 구입하면 구매 주식을 소유한다") {
        val holding = StockHolding()
        holding.buy("APPL", 50, 50)

        assertEq(holding.purchasesMap["APPL"]!!.size, 1)
    }

    test("하나의 주식에 대해 여러 번 구매 가능하다") {
        val holding = StockHolding()
        holding.buy("APPL", 50, 50)
        holding.buy("APPL", 10, 50)

        assertEq(holding.purchasesMap["APPL"]?.size, 2)
        assertEq(holding.purchasesMap["APPL"]?.sumOf {it.quantity}, 60)
    }

    test("보유 주식보다 많은 주식을 매도할 수 없다") {
        val holding = StockHolding(mutableMapOf("APPL" to mutableListOf(Purchase("APPL", 10, 10))))
        assertThrows<IllegalArgumentException> {
            holding.sell("APPL", 11)
        }
    }

    test("하나의 매수분 수량 보다 작게 매도하는 경우 해당 매수분의 수량이 줄어든다") {
        val holding = StockHolding(mutableMapOf("APPL" to mutableListOf(
            Purchase("APPL", 10, 10),
            Purchase("APPL", 20, 50),
        )
        )
        )
        holding.sell("APPL", 5)

        assertEq(holding.purchasesMap["APPL"]?.size, 2)
        assertEq(holding.purchasesMap["APPL"]?.first()?.quantity, 5)
    }

    test("하나의 매수분 수량 보다 크게 매도하는 경우 해당 매수분이 제거된다") {
        val holding = StockHolding(mutableMapOf("APPL" to mutableListOf(
            Purchase("APPL", 10, 10),
            Purchase("APPL", 20, 50),
        )
        )
        )
        holding.sell("APPL", 11)

        assertEq(holding.purchasesMap["APPL"]?.size, 1)
        assertEq(holding.purchasesMap["APPL"]?.first()?.quantity, 19)
    }

    test("전체 포트폴리오 조회 시 보유 수량 많은 순으로 정렬한다") {
        val holding = StockHolding(mutableMapOf(
            "APPL" to mutableListOf(Purchase("APPL", 10, 200), Purchase("APPL", 10, 300)),
            "NVDI" to mutableListOf(Purchase("NVDI", 20, 450), Purchase("NVDI", 10, 500)),
        ))
        val portfolio = Portfolio(holding)
        val stocks = portfolio.findAllStocks()

        assertEq(stocks.first().code, "NVDI")
        assertEq(stocks.last().code, "APPL")
    }

    test("보유 수량이 같으면 주식 코드 오름차순으로 정렬한다") {
        val holding = StockHolding(mutableMapOf(
            "APPL" to mutableListOf(Purchase("APPL", 20, 200), Purchase("APPL", 10, 300)),
            "NVDI" to mutableListOf(Purchase("NVDI", 20, 450), Purchase("NVDI", 10, 500)),
        ))
        val portfolio = Portfolio(holding)
        val stocks = portfolio.findAllStocks()

        assertEq(stocks.first().code, "APPL")
        assertEq(stocks.last().code, "NVDI")
    }
}

// test helper method
private fun <T> assertEq(actual: T, expected: T) {
    if(expected != actual) {
        println("❌ FAIL: expected: $expected, actual: $actual")
        throw TestFailedException()
    }
}

private fun assertNotNull(actual: Any?) {
    if (actual == null) {
        println("❌ FAIL: Assert not null failed")
        throw TestFailedException()
    }
}

private inline fun <reified T: Throwable> assertThrows(block: () -> Unit) {
    var capture: Throwable? = null
    try {
        block()
    } catch(e: Throwable) {
        capture = e
    }
    when {
        capture == null -> {
            println("❌ FAIL: Expect exception throws but not was thrown")
            throw TestFailedException()
        }
        capture is T -> {
            return
        }
        else -> {
            println("❌ FAIL: Expect ${T::class.simpleName} throws but ${capture::class.simpleName} was thrown")
            throw TestFailedException()
        }
    }
}

private inline fun test(testTitle: String, block: () -> Unit) {
    try {
        block()
        println("✅ PASS: $testTitle")
    } catch(e: Throwable) {
        println("Fail in \"$testTitle\", ${e.message}")
    }
}

class TestFailedException(msg: String = ""): Exception(msg)