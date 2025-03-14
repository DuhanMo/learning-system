package io.duhan.springkafka

import java.util.concurrent.CountDownLatch
import kotlin.system.exitProcess
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter
import org.springframework.kafka.support.converter.JsonMessageConverter
import org.springframework.kafka.support.converter.RecordMessageConverter

@SpringBootApplication
class SpringKafkaApplication {

    companion object {
        val LATCH = CountDownLatch(1)

        @JvmStatic
        fun main(args: Array<String>) {
            val context: ConfigurableApplicationContext = runApplication<SpringKafkaApplication>(*args)
            LATCH.await()
            Thread.sleep(5_000)
            context.close()
            exitProcess(0)
        }
    }

    @Bean
    fun converter(): RecordMessageConverter = JsonMessageConverter()

    @Bean
    fun batchConverter(): BatchMessagingMessageConverter = BatchMessagingMessageConverter(converter())

    @Bean
    fun topic2() = TopicBuilder.name("topic2").partitions(1).replicas(1).build()

    @Bean
    fun topic3() = TopicBuilder.name("topic3").partitions(1).replicas(1).build()
}