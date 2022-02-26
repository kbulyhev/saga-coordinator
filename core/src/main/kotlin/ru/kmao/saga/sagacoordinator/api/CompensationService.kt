package ru.kmao.saga.sagacoordinator.api

import ru.kmao.saga.sagacoordinator.dto.CompensationTransactionDTO
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity
import ru.kmao.saga.sagacoordinator.entity.model.ErrorTransactionDataModel

interface CompensationService {
    fun compensateTransaction(
        transactionId: String,
        sagaId: String,
        failedTransaction: SagaTransactionEntity
    ): Boolean

    fun beforeCompensation(
        compensationTransactionDTO: CompensationTransactionDTO,
        failedTransaction: SagaTransactionEntity,
        sagaId: String
    )

    fun compensateTransactionFromJob(errorTransactionDataModel: ErrorTransactionDataModel)
}