package duhan.io.distributedlock.config

import com.redis.testcontainers.RedisContainer
import com.redis.testcontainers.RedisContainer.DEFAULT_IMAGE_NAME
import com.redis.testcontainers.RedisContainer.DEFAULT_TAG
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class AbstractTestContainerBase {
    companion object {
        @JvmField
        @Container
        val redisContainer: RedisContainer = RedisContainer("$DEFAULT_IMAGE_NAME:$DEFAULT_TAG").apply {
            withExposedPorts(6379)
        }

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            val host = redisContainer.host
            val port = redisContainer.getMappedPort(6379)
            registry.add("redis.host") { host }
            registry.add("redis.port") { port }
        }
    }
}
