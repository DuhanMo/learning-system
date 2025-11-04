package com.system.batch.killbatchsystem.config

import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

/**
 * DefaultBatchConfiguration을 상속해야
 * JobRepository, JobLauncher 등 Spring Batch의 핵심 컴포넌트들을 자동으로 구성
 */
@Configuration
class BatchConfig : DefaultBatchConfiguration(){

    /**
     * Database 관련 Bean 생성
     * Spring Batch는 내부적으로 Job/Step 실행 상태를 DB 테이블에 저장함
     * 여기서는 개발/테스트 용도로 H2(인메모리 DB)를 사용
     */
    @Bean
    fun dataSource(): DataSource {
        return EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("org/springframework/batch/core/schema-h2.sql")
            .build()
    }

    /**
     * 트랜잭션 매니저 Bean 생성
     * Spring Batch는 Step 실행 중 트랜잭션을 적극적으로 사용
     * (Chunk 단위 커밋/롤백이 이걸 기반으로 작동)
     */
    @Bean
    fun transactionManager(): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource())
    }
}