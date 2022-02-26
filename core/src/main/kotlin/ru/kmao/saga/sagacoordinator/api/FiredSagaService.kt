package ru.kmao.saga.sagacoordinator.api

import ru.kmao.saga.sagacoordinator.entity.FiredSagaEntity

interface FiredSagaService {
    fun createSagaInfo(): FiredSagaEntity
    fun deleteById(sagaId: String)
}