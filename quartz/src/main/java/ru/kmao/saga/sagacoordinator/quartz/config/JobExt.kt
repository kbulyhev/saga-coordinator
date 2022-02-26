package ru.kmao.saga.sagacoordinator.quartz.config

import org.quartz.CronTrigger
import org.quartz.Job
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.springframework.scheduling.quartz.CronTriggerFactoryBean
import org.springframework.scheduling.quartz.JobDetailFactoryBean
import java.util.Calendar

fun createJobDetail(
    jobClass: Class<out Job>, jobName: String, jobGroup: String, jobDataMap: JobDataMap
): JobDetail {
    val factoryBean = JobDetailFactoryBean()
    factoryBean.setName(jobName)
    factoryBean.setJobClass(jobClass)
    factoryBean.setGroup(jobGroup)
    factoryBean.setDurability(true)
    factoryBean.afterPropertiesSet()
    factoryBean.jobDataMap = jobDataMap
    factoryBean.afterPropertiesSet()
    return factoryBean.getObject()!!
}

fun createCronTrigger(
    jobDetail: JobDetail,
    cronExpression: String,
    triggerName: String,
    jobGroup: String
): CronTrigger {


    // To fix an issue with time-based cron jobs
    val calendar: Calendar = Calendar.getInstance()
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    val factoryBean = CronTriggerFactoryBean()
    factoryBean.setJobDetail(jobDetail)
    factoryBean.setCronExpression(cronExpression)
    factoryBean.setStartTime(calendar.time)
    factoryBean.setStartDelay(0L)
    factoryBean.setName(triggerName)
    factoryBean.setGroup(jobGroup)
    factoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING)
    factoryBean.afterPropertiesSet()
    return factoryBean.getObject()!!
}
