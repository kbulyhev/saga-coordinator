package ru.kmao.saga.sagacoordinator.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "saga.resttemplate")
data class RestTemplateConfigProperties(
    val connectionTimeout: Long,
    val readTimeout: Long
)
