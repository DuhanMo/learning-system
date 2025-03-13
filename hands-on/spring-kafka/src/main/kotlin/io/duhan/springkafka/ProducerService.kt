package io.duhan.springkafka

import io.duhan.springkafka.dto.Foo1
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ProducerService(
    private val kafkaTemplate: KafkaTemplate<Any, Any>,
) {
    private val logger = getLogger(this.javaClass)

    fun sendFoo(what: String) {
        logger.info("Sending $what")
        kafkaTemplate.send("topic1", Foo1(what))
    }
}