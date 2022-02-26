package ru.kmao.saga.sagacoordinator.quartz.api

import org.springframework.stereotype.Service

@Service
interface SagaJobService {
    fun executeErrorSagaJob()
}