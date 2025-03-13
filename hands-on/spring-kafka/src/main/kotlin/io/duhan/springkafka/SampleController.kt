package io.duhan.springkafka

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController(
    private val producerService: ProducerService
) {
    @PostMapping("/send/foo/{what}")
    fun sendFoo(@PathVariable("what") what: String) {
        producerService.sendFoo(what)
    }
}