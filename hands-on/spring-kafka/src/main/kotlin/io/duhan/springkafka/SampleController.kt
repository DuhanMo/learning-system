package io.duhan.springkafka

import io.duhan.springkafka.dto.Bar1
import io.duhan.springkafka.dto.Foo1
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController(
    private val template: KafkaTemplate<Any, Any>,
) {
    private val logger = getLogger(this.javaClass)

    @PostMapping("/send/foo/{what}")
    fun sendFoo(@PathVariable what: String) {
        logging(what)
        template.send("foos", Foo1(what))
    }

    @PostMapping("/send/bar/{what}")
    fun sendBar(@PathVariable what: String) {
        logging(what)
        template.send("bars", Bar1(what))
    }

    @PostMapping("/send/unknown/{what}")
    fun sendUnknown(@PathVariable what: String) {
        logging(what)
        template.send("bars", what)
    }

    private fun logging(what: String) {
        logger.info("Send: $what")
    }
}