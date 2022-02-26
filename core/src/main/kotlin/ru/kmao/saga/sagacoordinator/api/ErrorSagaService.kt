package ru.kmao.saga.sagacoordinator.api

import ru.kmao.saga.sagacoordinator.entity.ErrorSagaEntity

interface ErrorSagaService {
    fun countByAttemptsToRecover(attemptsToRecover: Int): Int
    fun findAllIds(attemptsToRecover: Int): List<String>
    fun deleteById(sagaId: String)
    fun updateAttemptsToRecover(sagaId: String)
    fun save(errorSagaService: ErrorSagaEntity): ErrorSagaEntity
}