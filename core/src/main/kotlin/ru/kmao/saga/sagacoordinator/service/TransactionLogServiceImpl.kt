package ru.kmao.saga.sagacoordinator.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.kmao.saga.sagacoordinator.api.TransactionLogService
import ru.kmao.saga.sagacoordinator.api.converter.TransactionLogConverter
import ru.kmao.saga.sagacoordinator.constant.LogState
import ru.kmao.saga.sagacoordinator.constant.LogStatus
import ru.kmao.saga.sagacoordinator.dto.EndTransactionRequestDTO
import ru.kmao.saga.sagacoordinator.dto.StartSagaRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionRequestDTO
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity
import ru.kmao.saga.sagacoordinator.entity.TransactionLogEntity
import ru.kmao.saga.sagacoordinator.entity.model.ErrorTransactionDataModel
import ru.kmao.saga.sagacoordinator.exception.SagaCoordinatorBaseException
import ru.kmao.saga.sagacoordinator.repository.TransactionLogRepository
import ru.kmao.saga.sagacoordinator.utils.TransactionLogUtils
import java.time.OffsetDateTime

@Service
class TransactionLogServiceImpl(
    private val transactionLogRepository: TransactionLogRepository,
    private val transactionLogConverter: TransactionLogConverter
) : TransactionLogService {

    private val logger = KotlinLogging.logger {}

    override fun saveStartTransactionLog(
        savedEntity: SagaTransactionEntity,
        transactionRequestDTO: StartSagaRequestDTO,
        sagaId: String
    ) {
        val initialTransactionLog =
            transactionLogConverter.getInitialOrCompletedTransaction(sagaId, LogStatus.SUCCESS, LogState.START_SAGA)
        transactionLogRepository.save(initialTransactionLog)

        val logName = TransactionLogUtils.getLogName(
            savedEntity.serviceName,
            savedEntity.transactionIndex,
            LogState.START_TRANSACTION
        )

        checkTransactionLogUnique(sagaId, logName)

        val startTransactionLog = transactionLogConverter.getTransactionLogToStartSaga(savedEntity, sagaId, logName)
        transactionLogRepository.save(startTransactionLog)
    }

    override fun saveFailedTransactionLog(
        transaction: SagaTransactionEntity,
        sagaId: String,
        logName: String
    ): TransactionLogEntity {
        val transactionLogEntity = TransactionLogEntity(
            null,
            transaction,
            sagaId,
            logName,
            LogState.FAILED_TRANSACTION,
            LogStatus.SUCCESS,
            OffsetDateTime.now()
        )

        return transactionLogRepository.save(transactionLogEntity)
    }

    override fun saveStartTransactionLog(
        savedEntity: SagaTransactionEntity,
        transactionRequestDTO: TransactionRequestDTO,
    ): TransactionLogEntity {
        val logName = TransactionLogUtils.getLogName(
            savedEntity.serviceName,
            savedEntity.transactionIndex,
            LogState.START_TRANSACTION
        )
        checkTransactionLogUnique(transactionRequestDTO.sagaId, logName)

        val entityToSave =
            transactionLogConverter.getTransactionLogToStartTransaction(transactionRequestDTO, savedEntity, logName)
        return transactionLogRepository.save(entityToSave)
    }

    override fun saveCompletedTransactionLog(
        endTransaction: EndTransactionRequestDTO,
        transactionEntity: SagaTransactionEntity
    ): TransactionLogEntity {

        val logName = TransactionLogUtils.getLogName(
            transactionEntity.serviceName,
            transactionEntity.transactionIndex,
            LogState.END_TRANSACTION
        )
        checkTransactionLogUnique(endTransaction.sagaId, logName)

        val entityToSave =
            transactionLogConverter.getCompletedTransactionLog(endTransaction, transactionEntity, logName)

        return transactionLogRepository.save(entityToSave)
    }

    override fun saveCompensationTransactionLog(
        transactionToCompensate: SagaTransactionEntity,
        sagaId: String,
        logStatus: LogStatus
    ): TransactionLogEntity {
        val logName = TransactionLogUtils.getLogName(
            transactionToCompensate.serviceName,
            transactionToCompensate.transactionIndex,
            LogState.COMPENSATION_TRANSACTION
        )

        checkTransactionLogUnique(sagaId, logName)

        val compensationTransactionLog =
            transactionLogConverter.getCompensationTransactionLog(transactionToCompensate, sagaId, logStatus, logName)

        return transactionLogRepository.save(compensationTransactionLog)
    }

    override fun saveCompensationTransactionLogNative(
        transactionId: String,
        transactionLogName: String,
        sagaId: String,
        state: String
    ) {


        checkTransactionLogUnique(sagaId, transactionLogName)

        //need to test
        transactionLogRepository.saveNative(transactionId, sagaId, transactionLogName, state, LogStatus.ERROR)
    }

    override fun saveInitialOrCompletedTransaction(sagaId: String, logStatus: LogStatus, logState: LogState) {

        checkTransactionLogUnique(sagaId, logState.name)

        val initialTransactionLog =
            transactionLogConverter.getInitialOrCompletedTransaction(sagaId, logStatus, logState)
        transactionLogRepository.save(initialTransactionLog)
    }

    override fun isTransactionLogExistsByUniqueIndex(sagaId: String, name: String): Boolean {
        return transactionLogRepository.existsByNameAndSagaId(sagaId, name)
    }

    override fun findLastErrorTransactionLog(sagaIds: Set<String>): List<ErrorTransactionDataModel> {
       return transactionLogRepository.findLastErrorTransactionLog(sagaIds, LogStatus.ERROR)
    }

    private fun checkTransactionLogUnique(sagaId: String, name: String) {
        transactionLogRepository.findBySagaIdAndName(sagaId, name)?.let {
            throw SagaCoordinatorBaseException("Such transaction log (with name $name and sagaId = ${sagaId}) already exist")
        }
    }

}