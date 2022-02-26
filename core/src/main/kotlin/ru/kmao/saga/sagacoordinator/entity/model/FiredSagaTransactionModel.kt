package ru.kmao.saga.sagacoordinator.entity.model

import ru.kmao.saga.sagacoordinator.entity.FiredSagaEntity
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity

data class FiredSagaTransactionModel(
    val sagaTransactionEntity: SagaTransactionEntity,
    val sagaEntity: FiredSagaEntity
)