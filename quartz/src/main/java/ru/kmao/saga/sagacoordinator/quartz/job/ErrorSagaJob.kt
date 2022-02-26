package ru.kmao.saga.sagacoordinator.quartz.job

import mu.KotlinLogging
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kmao.saga.sagacoordinator.quartz.api.SagaJobService

@Service
@DisallowConcurrentExecution
class ErrorSagaJob : Job {

    private val logger = KotlinLogging.logger {}

    @Autowired
    private lateinit var sagaJobService: SagaJobService

    override fun execute(context: JobExecutionContext) {

        logger.info(
            "** Job ${context.jobDetail.key.name} ** firing @ ${context.fireTime}",
            context.jobDetail.key.name,
            context.fireTime
        )

        sagaJobService.executeErrorSagaJob()

    }
}