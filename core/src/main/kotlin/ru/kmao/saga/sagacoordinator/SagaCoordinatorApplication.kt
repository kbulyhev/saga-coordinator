package ru.kmao.saga.sagacoordinator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.kmao.saga.sagacoordinator.config.RestTemplateConfigProperties

@SpringBootApplication
@EnableConfigurationProperties(RestTemplateConfigProperties::class)
class SagaCoordinatorApplication

fun main(args: Array<String>) {
    runApplication<SagaCoordinatorApplication>(*args)
}
