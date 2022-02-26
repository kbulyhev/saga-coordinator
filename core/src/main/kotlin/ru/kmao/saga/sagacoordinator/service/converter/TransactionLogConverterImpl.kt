package ru.kmao.saga.sagacoordinator.service.converter

import org.springframework.stereotype.Service
import ru.kmao.saga.sagacoordinator.api.converter.TransactionLogConverter
import ru.kmao.saga.sagacoordinator.constant.LogState
import ru.kmao.saga.sagacoordinator.constant.LogStatus
import ru.kmao.saga.sagacoordinator.dto.EndTransactionRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionRequestDTO
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity
import ru.kmao.saga.sagacoordinator.entity.TransactionLogEntity
import java.time.OffsetDateTime

@Service
class TransactionLogConverterImpl : TransactionLogConverter {

    override fun getCompensationTransactionLog(
        transactionToCompensate: SagaTransactionEntity,
        sagaId: String,
        logStatus: LogStatus,
        logName: String
    ): TransactionLogEntity {

        return TransactionLogEntity(
            null,
            transaction = transactionToCompensate,
            sagaId = sagaId,
            name = logName,
            state = LogState.COMPENSATION_TRANSACTION,
            status = logStatus,
            timestamp = OffsetDateTime.now()
        )

    }

    override fun getCompletedTransactionLog(
        endTransaction: EndTransactionRequestDTO,
        transactionEntity: SagaTransactionEntity?,
        logName: String
    ): TransactionLogEntity {
        return with(endTransaction) {
            TransactionLogEntity(
                null,
                transaction = transactionEntity,
                sagaId = sagaId,
                name = logName,
                state = LogState.END_TRANSACTION,
                status = LogStatus.valueOf(logStatus),
                timestamp = OffsetDateTime.now()
            )
        }
    }

    override fun getTransactionLogToStartTransaction(
        transactionRequestDTO: TransactionRequestDTO,
        savedEntity: SagaTransactionEntity,
        logName: String
    ): TransactionLogEntity {
        return with(transactionRequestDTO) {
            TransactionLogEntity(
                null,
                transaction = savedEntity, sagaId = sagaId, name = logName,
                state = LogState.START_TRANSACTION,
                status = LogStatus.valueOf(logStatus),
                timestamp = OffsetDateTime.now()
            )
        }
    }

    override fun getInitialOrCompletedTransaction(
        sagaId: String,
        logStatus: LogStatus,
        logState: LogState
    ): TransactionLogEntity {
        return TransactionLogEntity(
            null,
            transaction = null,
            sagaId = sagaId,
            name = logState.name,
            state = logState,
            status = logStatus,
            timestamp = OffsetDateTime.now()
        )
    }

    override fun getTransactionLogToStartSaga(
        savedEntity: SagaTransactionEntity,
        sagaId: String,
        transactionLogName: String
    ): TransactionLogEntity {
        return TransactionLogEntity(
            null,
            transaction = savedEntity,
            sagaId = sagaId,
            name = transactionLogName,
            state = LogState.START_TRANSACTION,
            status = LogStatus.SUCCESS,
            timestamp = OffsetDateTime.now()
        )
    }
}