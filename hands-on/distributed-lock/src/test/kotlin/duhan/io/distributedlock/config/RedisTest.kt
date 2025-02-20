package duhan.io.distributedlock.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class RedisTest : AbstractTestContainerBase() {

    @Test
    fun `container is running`() {
        assertThat(redisContainer.isRunning).isTrue
    }
}