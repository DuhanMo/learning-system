package duhan.io.distributedlock

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coupons")
class CouponController(
    private val service: CouponService,
) {
    @PostMapping("/without-lock")
    fun issueWithoutLock(
        @RequestBody id: Long,
    ): ResponseEntity<Any> {
        service.issueWithoutLock(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/with-lock")
    fun issueWithLock(
        @RequestBody id: Long,
    ): ResponseEntity<Any> {
        service.issueWithLock(id)
        return ResponseEntity.noContent().build()
    }
}