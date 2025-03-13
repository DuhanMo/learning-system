package io.duhan.springkafka

import io.duhan.springkafka.dto.Bar2
import io.duhan.springkafka.dto.Foo2
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.CommonErrorHandler
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.converter.JsonMessageConverter
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper.TypePrecedence
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConfig {

    @Bean
    fun errorHandler(template: KafkaTemplate<Any, Any>): CommonErrorHandler =
        DefaultErrorHandler(DeadLetterPublishingRecoverer(template), FixedBackOff(1_000L, 2))

    @Bean
    fun converter(): RecordMessageConverter {
        val converter = JsonMessageConverter()
        val typeMapper = DefaultJackson2JavaTypeMapper()
        typeMapper.typePrecedence = TypePrecedence.TYPE_ID
        typeMapper.addTrustedPackages("io.duhan.springkafka.dto")
        val mappings: MutableMap<String, Class<*>> = HashMap()
        mappings["foo"] = Foo2::class.java
        mappings["bar"] = Bar2::class.java
        typeMapper.idClassMapping = mappings
        converter.typeMapper = typeMapper
        return converter
    }

    @Bean
    fun foos(): NewTopic = NewTopic("foos", 1, 1)

    @Bean
    fun bars(): NewTopic = NewTopic("bars", 1, 1)

}