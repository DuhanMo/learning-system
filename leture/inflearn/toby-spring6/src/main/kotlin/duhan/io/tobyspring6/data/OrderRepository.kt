package duhan.io.tobyspring6.data

import duhan.io.tobyspring6.order.Order
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

class OrderRepository {
    @PersistenceContext
    lateinit var entityManager: EntityManager

    fun save(order: Order) = entityManager.persist(order)
}