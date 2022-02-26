package ru.kmao.saga.sagacoordinator.api

import ru.kmao.saga.sagacoordinator.entity.FiredSagaEntity

interface SagaService {
    fun createFiredSagaEntity(): FiredSagaEntity
    fun deleteFromFiredSagaById(sagaId: String)
    fun deleteFromFiredAndSaveToErrorSaga(firedSagaEntity: FiredSagaEntity)
    fun deleteFromFiredAndSaveToSuccessSaga(firedSagaEntity: FiredSagaEntity)
}