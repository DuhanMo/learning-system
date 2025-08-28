import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.time.*
import java.io.*

import kotlin.IllegalArgumentException
fun main() {
    runTests()
}

private inline fun test(msg: String, block: () -> Unit) {
    try {
        block()
        println("✅ PASS: $msg")
    } catch(e: TestFailedException) {
        println("❌ FAIL: $msg, ${e.message}")
    }
}
private fun assertThat(condition: Boolean) {
    if (!condition) {
        throw TestFailedException("Assertion Failed")
    }
}
private inline fun <reified T : Throwable> assertThrows(block: () -> Unit) {
    var captured:Throwable? = null
    try {
        block()
    } catch(e: Throwable) {
        captured = e
    }
    when {
        captured == null -> throw TestFailedException("아무런 예외도 발생하지 않았습니다.")
        captured is T -> return
        else -> throw TestFailedException("기대한 예외: ${T::class.simpleName}, 발생한 예외: ${captured::class.simpleName}")
    }
}

class TestFailedException(msg: String): Exception(msg)

enum class OrderType {
    BUY, SELL,
}
object OrderIdGenerator {
    val id = AtomicLong()
    fun generate() = id.incrementAndGet()
    fun clear() = id.set(0)
}
// 주문
data class Order(
    val code: String,
    val type: OrderType,
    val quantity: Int,
    val amount:Int,
    var remain: Int = quantity,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val id: Long =  OrderIdGenerator.generate()
) {
    init {
        require(quantity > 0 && amount > 0) { "주문의 수량과 가격은 양수이어야 합니다." }
    }
    val isFullExecute: Boolean
        get() = remain == 0
}

data class Execute(
    val buyOrderId: Long,
    val sellOrderId: Long,
    val quantity: Int,
    val amount: Int,
)

data class PriceOrder(
    val price: Int,
    val quantity: Int,
)

// 주문 시스템
class OrderSystem(
    val buysMap: MutableMap<String, MutableList<Order>> = mutableMapOf(),
    val sellsMap: MutableMap<String, MutableList<Order>> = mutableMapOf(),
    val executes: MutableList<Execute> = mutableListOf(),
) {
    fun buy(code: String, quantity: Int, amount: Int) {
        val buys = buysMap.getOrPut(code) {mutableListOf()}
        buys.add(Order(code, OrderType.BUY, quantity, amount))
        match(code)
    }

    fun sell(code: String, quantity: Int, amount: Int) {

        val sells = sellsMap.getOrPut(code) {mutableListOf()}
        sells.add(Order(code, OrderType.SELL, quantity, amount))
        match(code)
    }

    private fun match(code: String) {
        while(true) {
            val bestBuy = buysMap[code]?.filter{!it.isFullExecute}?.maxWithOrNull(compareByDescending<Order> { it.amount }.thenBy { it.createdAt })
            val bestSell = sellsMap[code]?.filter{!it.isFullExecute}?.minWithOrNull(compareBy<Order> { it.amount }.thenBy { it.createdAt })
            if(bestBuy == null || bestSell == null) {
                break
            }
            if(bestBuy.amount < bestSell.amount) {
                break
            }
            val executeQuantity = minOf(bestBuy.quantity, bestSell.quantity)
            val executeAmount = bestSell.amount

            // 잔여수량 차감
            bestBuy.remain -= executeQuantity
            bestSell.remain -= executeQuantity

            // 체결 주문 기록
            executes.add(Execute(buyOrderId= bestBuy.id, sellOrderId = bestSell.id, quantity = executeQuantity, amount = executeAmount))

            // 완전 체결된 주문은 제거
            buysMap[code]?.removeAll {it.isFullExecute}
            sellsMap[code]?.removeAll {it.isFullExecute}
        }
    }

    fun findAllBuyOrder(code: String):List<PriceOrder> {
        val buys = buysMap[code] ?: return emptyList()
        return buys.filter { !it.isFullExecute }
            .groupBy { it.amount }
            .map {(amount, orders) ->
                PriceOrder(amount, orders.sumOf {it.remain})
            }.sortedByDescending { it.price }
    }

    fun findAllSellOrder(code: String):List<PriceOrder> {
        val sells = sellsMap[code] ?: return emptyList()
        return sells.filter { !it.isFullExecute }
            .groupBy { it.amount }
            .map {(amount, orders) ->
                PriceOrder(amount, orders.sumOf {it.remain})
            }.sortedByDescending { it.price }
    }
}


private fun runTests() {
    test("주문의 수량이 양수가 아닐 시 예외 발생한다") {
        assertThrows<IllegalArgumentException> {Order(code = "APPL", quantity = -1, amount = 100, type = OrderType.BUY)}
    }
    test("주문의 가격이 양수가 아닐 시 예외 발생한다") {
        assertThrows<IllegalArgumentException> {Order(code = "APPL",quantity = 5, amount = -100, type = OrderType.BUY)}
    }
    test("주문 아이디는 자동 생성되며 1씩 증가한다") {
        OrderIdGenerator.clear()
        val order1 = Order(code = "APPL", quantity = 1, amount = 100, type = OrderType.BUY)
        val order2 = Order(code = "APPL", quantity = 2, amount = 100, type = OrderType.BUY)
        assertThat(order1.id == 1L)
        assertThat(order2.id == 2L)
    }
    test("시스템에 매수 주문을 등록하면 시스템의 매수 목록의 사이즈가 증가한다") {
        val system = OrderSystem()

        system.buy("APPL", 1, 50)
        system.buy("APPL", 1, 51)

        assertThat(system.buysMap["APPL"]!!.size == 2)
    }
    test("시스템에 매도 주문을 등록하면 시스템의 매도 목록의 사이즈가 증가한다") {
        val system = OrderSystem()

        system.sell("APPL", 1, 50)
        system.sell("APPL", 1, 51)
        system.sell("APPL", 1, 52)

        assertThat(system.sellsMap["APPL"]!!.size == 3)
    }
    test("시스템에 매도주문만 존재한다면 매칭이 성사되지 않는다") {
        val system = OrderSystem()

        system.sell("APPL", 1, 50)
        system.sell("APPL", 1, 51)
        system.sell("APPL", 1, 52)

        assertThat(system.executes.size == 0)
    }
    test("시스템에 매수주문만 존재한다면 매칭이 성사되지 않는다") {
        val system = OrderSystem()

        system.buy("APPL", 1, 50)
        system.buy("APPL", 1, 51)
        system.buy("APPL", 1, 52)

        assertThat(system.executes.size == 0)
    }
    test("시스템에 매수가가 매도가보다 낮다면 매칭이 성사되지 않는다") {
        val system = OrderSystem()
        system.sell("APPL", 10, 50)
        system.sell("APPL", 10, 51)

        system.buy("APPL", 10, 40)

        assertThat(system.executes.size == 0)
    }
    test("시스템에 매수가가 매도가보다 높거나 같다면 매칭이 성사된다") {
        val system = OrderSystem()
        system.sell("APPL", 10, 50)
        system.sell("APPL", 10, 51)

        system.buy("APPL", 10, 100)

        assertThat(system.executes.size == 1)
        assertThat(system.executes.first().quantity == 10)
        assertThat(system.executes.first().amount == 50)
    }
    test("시스템에 하나의 매수주문이 여러 매도주문과 체결될 수 있다") {
        val system = OrderSystem()
        system.sell("APPL", 10, 50)
        system.sell("APPL", 5, 51)

        system.buy("APPL", 20, 100)

        assertThat(system.executes.size == 2)
        assertThat(system.executes[0].quantity == 10) // 첫번째 체결, 첫번째 매도주문의 수량
        assertThat(system.executes[0].amount == 50) // 첫번째 매도주문의 금액
        assertThat(system.executes[1].quantity == 5) // 두번째 체결, 두번째 매도주문의 수량
        assertThat(system.executes[1].amount == 51) // 두번째 매도주문의 금액
    }
    test("매수주문 조회하면 가격으로 수량이 그룹핑된다") {
        val system = OrderSystem()
        system.buy("APPL", 1, 100) // 100원짜리 1주
        system.buy("APPL", 2, 100) // 100원짜리 2주
        system.buy("APPL", 10, 50) // 50원짜리 10주

        val list = system.findAllBuyOrder("APPL")

        assertThat(list[0].quantity == 3)
        assertThat(list[0].price == 100)
        assertThat(list[1].quantity == 10)
        assertThat(list[1].price == 50)
    }
    test("매도주문 조회하면 가격 내림차순으로 정렬된다") {
        val system = OrderSystem()
        system.sell("APPL", quantity = 1, amount = 500)
        system.sell("APPL", quantity = 2, amount = 300)
        system.sell("APPL", quantity = 3, amount = 100)

        val list = system.findAllSellOrder("APPL")

        val isPriceDescending = list.zipWithNext {a, b -> a.price > b.price}.all { it }
        assertThat(isPriceDescending)
    }
}

