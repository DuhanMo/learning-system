package com.system.batch.killbatchsystem.job

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class SystemTerminatorConfig {
    @Bean
    fun processTerminatorJob(jobRepository: JobRepository, terminationStep: Step): Job {
        return JobBuilder("processTerminatorJob", jobRepository)
            .start(terminationStep)
            .build()
    }

    @Bean
    fun terminationStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        terminatorTasklet: Tasklet,
    ): Step {
        return StepBuilder("terminationStep", jobRepository)
            .tasklet(terminatorTasklet, transactionManager)
            .build()
    }

    @Bean
    @StepScope
    fun terminatorTasklet(
        @Value("#{jobParameters['terminatorId']}") terminatorId: String?,
        @Value("#{jobParameters['targetCount']}") targetCount: Int?,
    ): Tasklet {
        return Tasklet { _, _ ->
            logger.info("시스템 종결자 정보:")
            logger.info("ID: $terminatorId")
            logger.info("제거 대상 수 : $targetCount")
            logger.info("⚡ SYSTEM TERMINATOR $terminatorId 작전을 개시합니다.")
            logger.info("☠ ${targetCount}개의 프로세스를 종료합니다.")

            for (i in 1..(targetCount ?: 0)) {
                logger.info("💀 프로세스 $i 종료 완료!")
            }
            logger.info("🎯 임무 완료: 모든 대상 프로세스가 종료되었습니다.")
            RepeatStatus.FINISHED
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}