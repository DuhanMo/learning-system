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
class TerminatorConfig {
    @Bean
    fun terminatorJob(jobRepository: JobRepository, terminationStepByDate: Step): Job {
        return JobBuilder("terminatorJob", jobRepository)
            .start(terminationStepByDate)
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
        @Value("#{jobParameters['questDifficulty']}") questDifficulty: QuestDifficulty,
    ): Tasklet {
        return Tasklet { _, _ ->
            logger.info("⚔️ 시스템 침투 작전 개시!")
            logger.info("임무 난이도: $questDifficulty")

            val baseReward = 100
            val rewardMultiplier = when (questDifficulty) {
                QuestDifficulty.EASY -> 1
                QuestDifficulty.NORMAL -> 2
                QuestDifficulty.HARD -> 3
            }
            val totalReward = baseReward * rewardMultiplier
            logger.info("💥 시스템 해킹 진행 중...")
            logger.info("🏆 시스템 장악 완료!")
            logger.info("💰 획득한 시스템 리소스: ${totalReward} 메가바이트")
            RepeatStatus.FINISHED
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}