package ru.kmao.saga.sagacoordinator.api

import ru.kmao.saga.sagacoordinator.constant.LogState
import ru.kmao.saga.sagacoordinator.constant.LogStatus
import ru.kmao.saga.sagacoordinator.dto.EndTransactionRequestDTO
import ru.kmao.saga.sagacoordinator.dto.StartSagaRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionRequestDTO
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity
import ru.kmao.saga.sagacoordinator.entity.TransactionLogEntity
import ru.kmao.saga.sagacoordinator.entity.model.ErrorTransactionDataModel

interface TransactionLogService {
    fun saveStartTransactionLog(
        savedEntity: SagaTransactionEntity,
        transactionRequestDTO: StartSagaRequestDTO,
        sagaId: String
    )

    fun saveStartTransactionLog(
        savedEntity: SagaTransactionEntity,
        transactionRequestDTO: TransactionRequestDTO
    ): TransactionLogEntity

    fun saveCompletedTransactionLog(
        endTransaction: EndTransactionRequestDTO,
        transactionEntity: SagaTransactionEntity
    ): TransactionLogEntity

    fun saveCompensationTransactionLog(
        transactionToCompensate: SagaTransactionEntity,
        sagaId: String,
        logStatus: LogStatus
    ): TransactionLogEntity

    fun saveInitialOrCompletedTransaction(sagaId: String, logStatus: LogStatus, logState: LogState)


    fun saveFailedTransactionLog(
        transaction: SagaTransactionEntity,
        sagaId: String,
        logName: String
    ): TransactionLogEntity

    fun isTransactionLogExistsByUniqueIndex(sagaId: String, name: String): Boolean
    fun findLastErrorTransactionLog(sagaIds: Set<String>): List<ErrorTransactionDataModel>
    fun saveCompensationTransactionLogNative(
        transactionId: String,
        transactionLogName: String,
        sagaId: String,
        state: String
    )
}