package ru.kmao.saga.sagacoordinator.service

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.kmao.saga.sagacoordinator.api.RestTemplateService

@Service
class RestTemplateServiceImpl : RestTemplateService {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    private val logger = KotlinLogging.logger {}

    override fun executePost(urlToCompensate: String, rollbackPayload: String): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val entity = HttpEntity<String>(rollbackPayload, headers)

        return restTemplate.exchange(urlToCompensate, HttpMethod.POST, entity, String::class.java)
    }
}