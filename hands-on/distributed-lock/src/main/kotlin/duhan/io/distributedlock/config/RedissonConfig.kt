package duhan.io.distributedlock.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig {
    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer()
            .setAddress("redis://localhost:6379")
            .setPassword("strongPassword")
            .setTimeout(10000)
            .setConnectionMinimumIdleSize(5)
            .setConnectionPoolSize(10)
        return Redisson.create(config)
    }
}