package duhan.io.distributedlock

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id

@Entity
class Coupon(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,
    val name: String,
    var stock: Int = 0,
)