package duhan.io.project.common

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class PersistenceAdapter(
    @get:AliasFor(annotation = Component::class)
    val value: String = ""
)
