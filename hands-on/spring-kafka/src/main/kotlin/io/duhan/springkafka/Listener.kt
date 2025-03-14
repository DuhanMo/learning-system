package io.duhan.springkafka

import io.duhan.springkafka.dto.Foo2
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class Listener(private val kafkaTemplate: KafkaTemplate<String, String>) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @KafkaListener(id = "fooGroup2", topics = ["topic2"])
    fun listen1(foos: List<Foo2>) {
        logger.info("Received: $foos")
        foos.forEach { kafkaTemplate.send("topic3", it.foo.uppercase()) }
        logger.info("Messages sent, hit Enter to commit tx")
        System.`in`.read()
    }

    @KafkaListener(id = "fooGroup3", topics = ["topic3"])
    fun listen2(input: List<String>) {
        logger.info("Received: $input")
        SpringKafkaApplication.LATCH.countDown()
    }
}