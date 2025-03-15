package io.duhan.springkafka

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class Topic4Listener {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @RetryableTopic(attempts = "5", backoff = Backoff(delay = 2_000L, maxDelay = 10_000L, multiplier = 2.0))
    @KafkaListener(id = "topic4group", topics = ["topic4"])
    fun listen(
        `in`: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.OFFSET) offset: Long,
    ) {
        logger.info("Received: $`in` from $topic @ $offset")
        if (`in`.startsWith("fail")) {
            throw RuntimeException("failed")
        }
    }

    @DltHandler
    fun listenDlt(
        `in`: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.OFFSET) offset: Long,
    ) {
        logger.info("DLT Received: $`in` from $topic @ $offset")
    }
}