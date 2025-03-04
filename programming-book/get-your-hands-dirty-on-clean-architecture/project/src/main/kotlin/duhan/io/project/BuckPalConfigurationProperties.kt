package duhan.io.project

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "buckpal")
data class BuckPalConfigurationProperties(
    val transferThreshold: Long,
)