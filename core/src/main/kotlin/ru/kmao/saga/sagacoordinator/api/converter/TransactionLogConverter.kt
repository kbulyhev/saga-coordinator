package ru.kmao.saga.sagacoordinator.api.converter

import ru.kmao.saga.sagacoordinator.constant.LogState
import ru.kmao.saga.sagacoordinator.constant.LogStatus
import ru.kmao.saga.sagacoordinator.dto.EndTransactionRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionRequestDTO
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity
import ru.kmao.saga.sagacoordinator.entity.TransactionLogEntity

interface TransactionLogConverter {

    fun getCompensationTransactionLog(
        transactionToCompensate: SagaTransactionEntity,
        sagaId: String,
        logStatus: LogStatus,
        logName: String
    ): TransactionLogEntity

    fun getCompletedTransactionLog(
        endTransaction: EndTransactionRequestDTO,
        transactionEntity: SagaTransactionEntity?,
        logName: String
    ): TransactionLogEntity

    fun getTransactionLogToStartTransaction(
        transactionRequestDTO: TransactionRequestDTO,
        savedEntity: SagaTransactionEntity,
        logName: String
    ): TransactionLogEntity

    fun getInitialOrCompletedTransaction(
        sagaId: String,
        logStatus: LogStatus,
        logState: LogState
    ): TransactionLogEntity

    fun getTransactionLogToStartSaga(
        savedEntity: SagaTransactionEntity,
        sagaId: String,
        transactionLogName: String
    ): TransactionLogEntity
}