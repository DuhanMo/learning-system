package duhan.io.distributedlock.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig(
    @Value("\${redis.host}") private val host: String,
    @Value("\${redis.port}") private val port: Int,
) {
    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer()
            .setAddress("redis://$host:$port")
            .setTimeout(10000)
            .setConnectionMinimumIdleSize(5)
            .setConnectionPoolSize(10)
        return Redisson.create(config)
    }
}