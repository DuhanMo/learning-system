package io.duhan.security.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admins")
class AdminController {
    @GetMapping
    fun hello(): String {
        return "hello admin"
    }
}
