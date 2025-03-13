package io.duhan.springkafka

import io.duhan.springkafka.dto.Foo2
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class ConsumerService {
    private val logger = getLogger(this.javaClass)

    @KafkaListener(id = "fooGroup", topics = ["topic1"])
    fun listen(foo: Foo2) {
        logger.info("Received: $foo")
        if (foo.foo.startsWith("fail")) {
            throw RuntimeException("failed")
        }
    }

    @KafkaListener(id = "fltGroup", topics = ["topic1.DLT"])
    fun dltListen(`in`: ByteArray) {
        logger.info("Received from DLT: ${String(`in`)}")
    }
}