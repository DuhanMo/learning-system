package duhan.io.tobyspring6

import duhan.io.tobyspring6.data.OrderRepository
import jakarta.persistence.EntityManagerFactory
import javax.sql.DataSource
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter

@Configuration
class DataConfig {
    @Bean
    fun dataSource(): DataSource = EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build()

    @Bean
    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val emf = LocalContainerEntityManagerFactoryBean()
        emf.dataSource = dataSource()
        emf.setPackagesToScan("duhan.io.tobyspring6")
        emf.jpaVendorAdapter = HibernateJpaVendorAdapter().apply {
            setDatabase(Database.H2)
            setGenerateDdl(true)
            setShowSql(true)
        }
        return emf
    }

    @Bean
    fun persistenceAnnotationBeanPostProcessor(): BeanPostProcessor = PersistenceAnnotationBeanPostProcessor()

    @Bean
    fun transactionManager(emf: EntityManagerFactory): JpaTransactionManager = JpaTransactionManager(emf)

    @Bean
    fun orderRepository(): OrderRepository = OrderRepository()
}