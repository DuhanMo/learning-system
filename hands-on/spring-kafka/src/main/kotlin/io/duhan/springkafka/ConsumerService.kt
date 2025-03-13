package io.duhan.springkafka

import io.duhan.springkafka.dto.Bar2
import io.duhan.springkafka.dto.Foo2
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
@KafkaListener(id = "multiGroup", topics = ["foos", "bars"])
class ConsumerService {
    private val logger = getLogger(this.javaClass)

    @KafkaHandler
    fun foo(foo: Foo2) {
        logger.info("Received foo: $foo")
    }

    @KafkaHandler
    fun bar(bar: Bar2) {
        logger.info("Received bar: $bar")
    }

    @KafkaHandler(isDefault = true)
    fun unknown(any: Any) {
        logger.info("Received any: $any")
    }
}