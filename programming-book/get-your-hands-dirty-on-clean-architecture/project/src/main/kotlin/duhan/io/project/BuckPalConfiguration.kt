package duhan.io.project

import duhan.io.project.application.domain.service.MoneyTransferProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(BuckPalConfigurationProperties::class)
class BuckPalConfiguration {
    @Bean
    fun moneyTransferProperties(properties: BuckPalConfigurationProperties): MoneyTransferProperties =
        MoneyTransferProperties()
}