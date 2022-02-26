package ru.kmao.saga.sagacoordinator.api

import ru.kmao.saga.sagacoordinator.constant.TransactionStatus
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity
import ru.kmao.saga.sagacoordinator.entity.model.FiredSagaTransactionModel


interface SagaTransactionService {
    fun save(startTransactionEntity: SagaTransactionEntity): SagaTransactionEntity
    fun findByIdOrThrowException(transactionId: String): SagaTransactionEntity
    fun updateTransactionStatus(transactionStatus: TransactionStatus, transactionId: String?)
    fun updateTransactionStatusAndPayload(
        transactionStatus: TransactionStatus,
        transactionId: String?,
        compensationPayload: String
    )

    fun updateCompensated(compensated: Boolean, transactionId: String?)
    fun updateTransactionStatusAndCompensated(
        transactionStatus: TransactionStatus,
        compensated: Boolean,
        transactionId: String?
    )

    fun updateCompensationPayload(compensationPayload: String, transactionId: String)
    fun findByIdWithFiredSagaThrowException(transactionId: String, sagaId: String): FiredSagaTransactionModel
}