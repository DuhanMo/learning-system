package com.system.batch.killbatchsystem.job

import com.system.batch.killbatchsystem.config.BatchConfig
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.transaction.PlatformTransactionManager
import java.util.concurrent.atomic.AtomicInteger

@Import(BatchConfig::class)
class SystemTerminationConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {
    private val processesKilled = AtomicInteger(0)
    private val TERMINATION_TARGET = 5

    /**
     * 시뮬레이션 Job 정의
     * Step 순서는 enter → meet → defeat 반복 → complete
     */
    @Bean
    fun systemTerminationSimulationJob(): Job {
        return JobBuilder("systemTerminationSimulationJob", jobRepository)
            .start(enterWorldStep())
            .next(meetNPCStep())
            .next(defeatProcessStep())
            .next(completeQuestStep())
            .build()
    }

    /**
     * Step 1: 배치 세계(시스템 시뮬레이션) 입장 로그 출력
     */
    @Bean
    fun enterWorldStep(): Step {
        return StepBuilder("enterWorldStep", jobRepository)
            .tasklet({ _, _ ->
                println("System Termination 시뮬레이션 세계에 접속했습니다!")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean
    fun meetNPCStep(): Step {
        return StepBuilder("meetNPCStep", jobRepository)
            .tasklet({ _, _ ->
                println("시스템 관리자 NPC를 만났습니다.")
                println("첫 번째 미션: 좀비 프로세스 $TERMINATION_TARGET 개 처형하기")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    /**
     * Step 3: 좀비 프로세스 처형 진행
     * TERMINATION_TARGET 개수에 도달할 때까지 CONTINUABLE 반환 → 반복 실행
     */
    @Bean
    fun defeatProcessStep(): Step {
        return StepBuilder("defeatProcessStep", jobRepository)
            .tasklet({ _, _ ->
                val terminated = processesKilled.incrementAndGet()
                println("좀비 프로세스 처형 완료! (현재 $terminated/$TERMINATION_TARGET)")
                if (terminated < TERMINATION_TARGET) {
                    RepeatStatus.CONTINUABLE
                } else {
                    RepeatStatus.FINISHED
                }
            }, transactionManager)
            .build()
    }

    /**
     * Step 4: 모든 처형 완료! 보상 출력
     */
    @Bean
    fun completeQuestStep(): Step {
        return StepBuilder("completeQuestStep", jobRepository)
            .tasklet({_,_ ->
                println("미션 완료! 좀비 프로세스 $TERMINATION_TARGET 개 처형 성공!")
                println("보상: kill -9 권한 획득, 시스템 제어 레벨 1 달성")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}