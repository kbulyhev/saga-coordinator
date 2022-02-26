package ru.kmao.saga.sagacoordinator.api

import org.springframework.http.ResponseEntity

interface RestTemplateService {
    fun executePost(urlToCompensate: String, rollbackPayload: String): ResponseEntity<String>
}