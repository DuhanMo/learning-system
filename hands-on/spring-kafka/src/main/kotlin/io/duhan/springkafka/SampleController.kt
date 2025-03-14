package io.duhan.springkafka

import io.duhan.springkafka.dto.Foo1
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class SampleController(private val template: KafkaTemplate<Any, Any>) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping("/send/foos/{what}")
    fun sendFoo(@PathVariable what: String) {
        logger.info("tx start: what=$what")
        // transaction only span to consumer side
        template.executeInTransaction { kafkaTemplate ->
            what.split(",").map { Foo1(it) }.forEach { foo ->
                kafkaTemplate.send("topic2", foo)
            }
            Unit
        }
    }
}