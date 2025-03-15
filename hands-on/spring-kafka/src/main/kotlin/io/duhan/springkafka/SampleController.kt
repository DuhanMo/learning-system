package io.duhan.springkafka

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class SampleController(private val kafkaTemplate: KafkaTemplate<Any, Any>) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping("/send/topic4/{what}")
    fun sendTopic4(@PathVariable what: String) {
        logger.info("Send topic4 $what")
        kafkaTemplate.send("topic4", what)
    }

    @PostMapping("/send/topic5/{what}")
    fun sendTopic5(@PathVariable what: String) {
        logger.info("Send topic5 $what")
        kafkaTemplate.send("topic5", what)
    }
}