package ru.kmao.saga.sagacoordinator.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class AppConfig {

    @Autowired
    private lateinit var restTemplateConfigProperties: RestTemplateConfigProperties

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate {

        return restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(restTemplateConfigProperties.connectionTimeout))
            .setReadTimeout(Duration.ofSeconds(restTemplateConfigProperties.connectionTimeout))
            .build()
    }
}