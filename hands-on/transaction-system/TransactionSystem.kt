import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong
import java.time.*;
import java.time.format.DateTimeFormatter

// To execute Kotlin code, please define a top level function named main

// 트랜잭션(거래 ID, 금액, 거래일시, 거래유형, 설명)
// 거래 ID는 1부터 시작하는 순차번호
// 출금/이체시 잔액 확인
enum class TransactionType {
    DEPOSIT, WITHDRAW, TRANSFER;
}

object TransactionIdGenerator {
    private val id = AtomicLong()

    fun generate(): Long = id.incrementAndGet()

    fun clear() {
        id.set(0)
    }
}

data class Transaction(
    val id: Long = TransactionIdGenerator.generate(),
    val amount: Long,
    val type: TransactionType,
    val description: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(amount > 0) { "거래 금액은 양수여야합니다."}
    }
}

// 잔고
class Balance(
    var amount: Long = 0,
) {
    // 잔고 계산 (출금시)
    private fun isPossibleExtract(minusAmount: Long): Boolean = amount >= minusAmount

    fun minus(amount: Long) {
        require(isPossibleExtract(amount)) {"잔액이 부족합니다."}
        this.amount -= amount
    }

    fun plus(amount: Long) {
        this.amount += amount
    }
}

data class TransactionSnapShot(
    val transactionId: Long,
    val amount: Long,
    val type: TransactionType,
    val balance: Long,
    val description: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun of(tx: Transaction, balance: Balance): TransactionSnapShot {
            return TransactionSnapShot(
                transactionId = tx.id,
                amount = tx.amount,
                type = tx.type,
                balance = balance.amount,
                description = tx.description,
                createdAt = tx.createdAt,
            )
        }
    }
}

// 트랜잭션 시스템
class TransactionSystem(
    val balance: Balance,
    val transactions: MutableList<Transaction> = mutableListOf(),
    val snapshots: MutableList<TransactionSnapShot> = mutableListOf(),
) {
    fun deposit(amount: Long, msg: String= "") {
        balance.plus(amount)
        val tx = Transaction(amount = amount, type = TransactionType.DEPOSIT, description = msg)
        val snapshot = TransactionSnapShot.of(tx, balance)
        transactions.add(tx)
        snapshots.add(snapshot)
    }

    fun withdraw(amount: Long, msg: String = "") {
        balance.minus(amount)
        val tx = Transaction(amount = amount, type = TransactionType.WITHDRAW, description = msg)
        val snapshot = TransactionSnapShot.of(tx, balance)
        transactions.add(tx)
        snapshots.add(snapshot)
    }

    fun transfer(amount: Long, target: String) {
        balance.minus(amount)
        val tx = Transaction(amount = amount, type = TransactionType.TRANSFER, description = "$target 에게 송금")
        val snapshot = TransactionSnapShot.of(tx, balance)
        transactions.add(tx)
        snapshots.add(snapshot)
    }

    fun getTransactionHistory(type: TransactionType?, from: LocalDateTime?, to: LocalDateTime?): List<TransactionView>{
        return snapshots
            .filter { snapshot ->
                val typeMatch = type?.let {snapshot.type == it} ?: true
                val periodMatch = when {
                    from != null && to != null -> snapshot.createdAt.isAfter(from) && snapshot.createdAt.isBefore(to)
                    from != null -> snapshot.createdAt.isAfter(from)
                    to != null -> snapshot.createdAt.isBefore(to)
                    else -> true
                }
                typeMatch && periodMatch
            }.sortedByDescending { it.transactionId }
            .map{ TransactionView.from(it) }
    }
}

data class TransactionView(
    val transactionId: Long,
    val amountWithSign: Long,
    val type: TransactionType,
    val balance: Long,
    val description: String,
    val createdAt: String,
) {
    companion object {
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")

        fun from(snapshot: TransactionSnapShot):TransactionView {
            return TransactionView(
                transactionId = snapshot.transactionId,
                amountWithSign = if (snapshot.type == TransactionType.DEPOSIT) snapshot.amount else -snapshot.amount,
                type = snapshot.type,
                balance = snapshot.balance,
                description = snapshot.description,
                createdAt = snapshot.createdAt.format(DATE_TIME_FORMATTER),
            )
        }
    }
}

fun main() {
    runTests()

}

private fun runTests() {
    test("트랜잭션 아이디는 생성할 때마다 숫자가 1씩 증가한다") {
        val id1 = TransactionIdGenerator.generate()
        val id2 = TransactionIdGenerator.generate()
        val id3 = TransactionIdGenerator.generate()

        assertThat(id1 == 1L)
        assertThat(id2 == 2L)
        assertThat(id3 == 3L)
    }

    test("입금금액이 양수가 아니면 예외 발생한다") {
        assertThrows<IllegalArgumentException>{ Transaction(id = 1L, type = TransactionType.DEPOSIT, amount =0)}
        assertThrows<IllegalArgumentException> {Transaction(id = 1L, type = TransactionType.DEPOSIT, amount =-10)}
    }

    test("입금하면 잔고가 증가한다") {
        val balance = Balance()
        val system = TransactionSystem(balance)
        system.deposit(1000)

        assertThat(balance.amount == 1000L)
    }

    test("입금하면 거래스냅샷이 추가된다") {
        val balance = Balance()
        val system = TransactionSystem(balance)
        system.deposit(1000)
        system.deposit(2000)

        assertThat(system.snapshots.size == 2)
    }

    test("잔고보다 많이 출금요청하면 예외 발생한다") {
        val balance = Balance(amount = 500)
        val system = TransactionSystem(balance)

        assertThrows<IllegalArgumentException> {system.withdraw(501)}
    }

    test("출금하면 잔고가 감소한다") {
        val balance = Balance(amount = 500)
        val system = TransactionSystem(balance)

        system.withdraw(450)

        assertThat(balance.amount == 50L)
    }

    test("잔고보다 많이 송금요청하면 예외 발생한다") {
        val balance = Balance(amount = 500)
        val system = TransactionSystem(balance)

        assertThrows<IllegalArgumentException> {system.transfer(501, "친구")}
    }

    test("송금하면 잔고가 감소한다") {
        val balance = Balance(amount = 500)
        val system = TransactionSystem(balance)

        system.transfer(450, "친구")

        assertThat(balance.amount == 50L)
    }

    test("전체 거래 내역을 최신순으로 조회한다") {
        val balance = Balance()
        val system = TransactionSystem(balance)

        system.deposit(1000)
        system.deposit(2000)
        system.withdraw(100)
        system.withdraw(100)
        system.transfer(100, "친구")

        val list = system.getTransactionHistory(null, null, null)
        val isDescending = list.zipWithNext {a, b -> a.transactionId > b.transactionId }.all { it }
        assertThat(isDescending)
    }

    test("거래 내역 필터링 - 타입") {
        val balance = Balance()
        val system = TransactionSystem(balance)

        system.deposit(1000)
        system.deposit(2000)
        system.withdraw(100)
        system.withdraw(100)
        system.transfer(100, "친구")

        val list = system.getTransactionHistory(TransactionType.DEPOSIT, null, null)

        val isAllFiltered = list.map {it.type}.all {it == TransactionType.DEPOSIT}
        assertThat(isAllFiltered)
    }

    test("거래 내역 필터링 - 시작일시") {
        val balance = Balance()
        val snapshot1 = TransactionSnapShot(
            transactionId = 1L,
            amount = 500,
            type = TransactionType.DEPOSIT,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 4, 1, 0, 0),
        )
        val snapshot2 = TransactionSnapShot(
            transactionId = 2L,
            amount = 500,
            type = TransactionType.DEPOSIT,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 5, 1, 0, 0),
        )
        val snapshot3 = TransactionSnapShot(
            transactionId = 3L,
            amount = 500,
            type = TransactionType.DEPOSIT,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 6, 1, 0, 0),
        )
        val snapshot4 = TransactionSnapShot(
            transactionId = 4L,
            amount = 500,
            type = TransactionType.DEPOSIT,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 7, 1, 0, 0),
        )
        val snapshots = mutableListOf(snapshot1, snapshot2,snapshot3,snapshot4)

        val system = TransactionSystem(balance =balance, snapshots = snapshots)


        val list = system.getTransactionHistory(type = null, from =  LocalDateTime.of(2025,4,3,0,0), to =  null)
        assertThat(list.size == 3)
        assertThat(list.map{it.transactionId} == listOf(4L,3L,2L))
    }

    test("거래 내역 필터링 - 종료일시") {
        val balance = Balance()
        val snapshot1 = TransactionSnapShot(
            transactionId = 1L,
            amount = 500,
            type = TransactionType.DEPOSIT,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 4, 1, 0, 0),
        )
        val snapshot2 = TransactionSnapShot(
            transactionId = 2L,
            amount = 500,
            type = TransactionType.DEPOSIT,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 5, 1, 0, 0),
        )
        val snapshot3 = TransactionSnapShot(
            transactionId = 3L,
            amount = 500,
            type = TransactionType.DEPOSIT,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 6, 1, 0, 0),
        )
        val snapshot4 = TransactionSnapShot(
            transactionId = 4L,
            amount = 500,
            type = TransactionType.DEPOSIT,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 7, 1, 0, 0),
        )
        val snapshots = mutableListOf(snapshot1, snapshot2,snapshot3,snapshot4)

        val system = TransactionSystem(balance =balance, snapshots = snapshots)


        val list = system.getTransactionHistory(type = null, from =  null, to =  LocalDateTime.of(2025,6,2,0,0))
        assertThat(list.size == 3)
        assertThat(list.map{it.transactionId} == listOf(3L,2L,1L))
    }

    test("거래 내역 필터링 - 타입 & 시작일시 & 종료일시") {
        val balance = Balance()
        val snapshot1 = TransactionSnapShot(
            transactionId = 1L,
            amount = 500,
            type = TransactionType.DEPOSIT,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 4, 1, 0, 0),
        )
        val snapshot2 = TransactionSnapShot(
            transactionId = 2L,
            amount = 500,
            type = TransactionType.DEPOSIT,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 5, 1, 0, 0),
        )
        val snapshot3 = TransactionSnapShot(
            transactionId = 3L,
            amount = 500,
            type = TransactionType.WITHDRAW,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 6, 1, 0, 0),
        )
        val snapshot4 = TransactionSnapShot(
            transactionId = 4L,
            amount = 500,
            type = TransactionType.DEPOSIT,
            balance = 500,
            description = "",
            createdAt= LocalDateTime.of(2025, 7, 1, 0, 0),
        )
        val snapshots = mutableListOf(snapshot1, snapshot2,snapshot3,snapshot4)

        val system = TransactionSystem(balance =balance, snapshots = snapshots)


        val list = system.getTransactionHistory(type = TransactionType.DEPOSIT, from =  LocalDateTime.of(2025,4,5,0,0), to =  LocalDateTime.of(2025,6,5,0,0))
        assertThat(list.size == 1)
        assertThat(list.map{it.transactionId} == listOf(2L))
    }
}


private inline fun test(msg: String, block: () -> Unit) {
    try {
        block()
        println("✅ PASS: $msg")
    } catch (e: TestFailedException) {
        println("❌ FAIL: $msg, ${e.message}")
    }
}
// 테스트 확인 헬퍼 함수
private fun assertThat(condition: Boolean) {
    if (!condition) {
        throw TestFailedException("Assert Failed")
    }
}

private inline fun <reified T : Throwable> assertThrows(block: () -> Unit) {
    var capturedException: Throwable? = null
    try {
        block()
    } catch (e: Throwable) {
        capturedException = e
    }
    when {
        capturedException == null -> {
            throw TestFailedException("Expected exeception but not was thrown")
        }
        capturedException is T -> {
            return
        }
        else -> {
            throw TestFailedException("Expected ${T::class.simpleName} but ${capturedException::class.simpleName} was thrown")
        }
    }
}

class TestFailedException(msg: String) : Exception(msg)