package ru.kmao.saga.sagacoordinator.quartz.config

import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.SchedulerException
import org.quartz.Trigger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.quartz.QuartzProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import ru.kmao.saga.sagacoordinator.quartz.constants.QuartzConstant
import ru.kmao.saga.sagacoordinator.quartz.job.ErrorSagaJob
import java.text.ParseException
import java.util.Properties
import javax.sql.DataSource

@Configuration
open class QuartzConfig {

    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var quartzProperties: QuartzProperties

    @Value("\${spring.quartz.saga.error-job.cron}")
    private lateinit var transactionJobCron: String

//    @Value("\${spring.quartz.automatic-scheduler-creation}")
//    private val automaticSchedulerCreation = false

    /**
     * create scheduler factory.
     *
     * @return the scheduler factory bean
     * @throws SchedulerException the scheduler exception
     * @throws ParseException     the parse exception
     */
    @Bean
    @Throws(SchedulerException::class, ParseException::class)
    open fun schedulerFactoryBean(): SchedulerFactoryBean {
        val jobFactory = SchedulerJobFactory()
        jobFactory.setApplicationContext(applicationContext)
        val properties = Properties()
        properties.putAll(quartzProperties.properties)
        val factory = SchedulerFactoryBean()
        factory.setOverwriteExistingJobs(true)
        factory.setDataSource(dataSource)
        factory.setQuartzProperties(properties)
        factory.setJobFactory(jobFactory)

        val migrationTaskJob = getErrorSagaJobDetail()
        factory.setJobDetails(migrationTaskJob)
        factory.setTriggers(getErrorSagaTriggerJob(migrationTaskJob))

        return factory
    }


    @Throws(ParseException::class)
    fun getErrorSagaTriggerJob(jobDetail: JobDetail): Trigger {
        return createCronTrigger(
            jobDetail, transactionJobCron,
            QuartzConstant.ERROR_SAGA_TRIGGER_NAME, QuartzConstant.SAGA_JOB_GROUP
        )
    }

    private fun getErrorSagaJobDetail(): JobDetail {
        return createJobDetail(
            ErrorSagaJob::class.java,
            QuartzConstant.ERROR_SAGA_JOB_NAME,
            QuartzConstant.SAGA_JOB_GROUP, JobDataMap()
        )
    }
}