package com.system.batch.killbatchsystem.job

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.transaction.PlatformTransactionManager

/**
 * ./gradlew bootRun --args='--spring.batch.job.name=systemFailureJob inputFile=/Users/moduhan/dev/learning-system/leture/inflearn/batch/kill-batch-system/system-failures.csv'
 */
@Configuration
class SystemFailureJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {
    @Bean
    fun systemFailureJob(systemFailureStep: Step): Job {
        return JobBuilder("systemFailureJob", jobRepository)
            .start(systemFailureStep)
            .build()
    }

    @Bean
    fun systemFailureStep(
        systemFailureItemReader: FlatFileItemReader<SystemFailure>,
        systemFailureStdoutItemWriter: SystemFailureStdoutItemWriter,
    ): Step {
        return StepBuilder("systemFailureStep", jobRepository)
            .chunk<SystemFailure, SystemFailure>(10, transactionManager)
            .reader(systemFailureItemReader)
            .writer(systemFailureStdoutItemWriter)
            .build()
    }

    @Bean
    @StepScope
    fun systemFailureItemReader(
        @Value("#{jobParameters['inputFile']}") inputFile: String
    ): FlatFileItemReader<SystemFailure> {
        return FlatFileItemReaderBuilder<SystemFailure>()
            .name("systemFailureItemReader")
            .resource(FileSystemResource(inputFile))
            .delimited()
            .delimiter(",")
            .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
            .targetType(SystemFailure::class.java)
            .linesToSkip(1)
            .build()
    }

    @Bean
    fun systemFailureStdoutItemWriter(): SystemFailureStdoutItemWriter {
        return SystemFailureStdoutItemWriter()
    }

    class SystemFailureStdoutItemWriter : ItemWriter<SystemFailure> {
        override fun write(chunk: Chunk<out SystemFailure?>) {
            for (failure in chunk) {
                logger.info("Processing system failure: $failure")
            }
        }
    }

    // FlatFileItemReader는 기본생성자와 setter가 필요하기 때문에 데이터클래스에 기본값 지정과 var 선언
    data class SystemFailure(
        var errorId: String = "",
        var errorDateTime: String = "",
        var severity: String = "",
        var processId: Int = 0,
        var errorMessage: String = "",
    )

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}
