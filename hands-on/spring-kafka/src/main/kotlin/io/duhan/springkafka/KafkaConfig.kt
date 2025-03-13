package io.duhan.springkafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.CommonErrorHandler
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.converter.JsonMessageConverter
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConfig {

    @Bean
    fun errorHandler(template: KafkaTemplate<Any, Any>): CommonErrorHandler =
        DefaultErrorHandler(DeadLetterPublishingRecoverer(template), FixedBackOff(1_000L, 2))

    @Bean
    fun converter(): RecordMessageConverter = JsonMessageConverter()

    @Bean
    fun topic(): NewTopic = NewTopic("topic1", 1, 1)

    @Bean
    fun dlt(): NewTopic = NewTopic("topic1.DLT", 1, 1)

}