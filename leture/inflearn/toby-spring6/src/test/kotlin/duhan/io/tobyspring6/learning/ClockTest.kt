package duhan.io.tobyspring6.learning

import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.shouldBe
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.jupiter.api.Test

class ClockTest {
    @Test
    fun clock() {
        val clock = Clock.systemDefaultZone()

        val now1 = LocalDateTime.now(clock)
        val now2 = LocalDateTime.now(clock)

        now2 shouldBeAfter now1
    }

    @Test
    fun fixedClock() {
        val fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault())

        val now1 = LocalDateTime.now(fixedClock)
        val now2 = LocalDateTime.now(fixedClock)

        val now3 = LocalDateTime.now(fixedClock).plusHours(1)

        now1 shouldBe now2
        now3 shouldBe now1.plusHours(1)
    }
}