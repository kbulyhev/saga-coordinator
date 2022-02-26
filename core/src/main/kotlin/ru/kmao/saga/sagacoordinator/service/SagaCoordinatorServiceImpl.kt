package ru.kmao.saga.sagacoordinator.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.kmao.saga.sagacoordinator.api.CompensationService
import ru.kmao.saga.sagacoordinator.api.SagaCoordinatorService
import ru.kmao.saga.sagacoordinator.api.SagaService
import ru.kmao.saga.sagacoordinator.api.SagaTransactionService
import ru.kmao.saga.sagacoordinator.api.TransactionLogService
import ru.kmao.saga.sagacoordinator.api.converter.SagaTransactionConverterService
import ru.kmao.saga.sagacoordinator.constant.LogState
import ru.kmao.saga.sagacoordinator.constant.LogStatus
import ru.kmao.saga.sagacoordinator.constant.TransactionStatus
import ru.kmao.saga.sagacoordinator.dto.CompensationTransactionDTO
import ru.kmao.saga.sagacoordinator.dto.EndTransactionRequestDTO
import ru.kmao.saga.sagacoordinator.dto.StartSagaRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionResponseDTO
import ru.kmao.saga.sagacoordinator.entity.model.FiredSagaTransactionModel
import javax.transaction.Transactional

@Service
class SagaCoordinatorServiceImpl(
    private val sagaTransactionService: SagaTransactionService,
    private val converterService: SagaTransactionConverterService,
    private val transactionLogService: TransactionLogService,
    private val compensationService: CompensationService,
    private val sagaService: SagaService
) : SagaCoordinatorService {

    private val logger = KotlinLogging.logger {}

    @Transactional
    override fun createSaga(transactionRequestDTO: StartSagaRequestDTO): TransactionResponseDTO {
        logger.debug { "Creating saga..." }

        val sagaInfo = sagaService.createFiredSagaEntity()
        val startTransactionEntity =
            converterService.getStartSagaTransactionFromDto(transactionRequestDTO, sagaInfo.sagaId)

        val savedEntity = sagaTransactionService.save(startTransactionEntity)

        val sagaId = sagaInfo.sagaId

        transactionLogService.saveStartTransactionLog(savedEntity, transactionRequestDTO, sagaInfo.sagaId!!)

        return TransactionResponseDTO(savedEntity.transactionId, sagaId!!, savedEntity.transactionIndex, null)
    }

//    @Transactional
//    override fun completeSaga(completeSagaDTO: CompleteSagaDTO) {
//        logger.debug { "completing saga where sagaId = ${completeSagaDTO.sagaId}" }
//
//        val transactionEntity = sagaTransactionService.findByIdOrThrowException(completeSagaDTO.transactionId)
//
//        transactionLogService.saveCompleteSagaLog(completeSagaDTO, transactionEntity)
//        sagaTransactionService.updateTransactionStatus(
//            TransactionStatus.valueOf(completeSagaDTO.transactionStatus), transactionEntity.transactionId
//        )
//    }

    @Transactional
    override fun startTransaction(transactionRequestDTO: TransactionRequestDTO): TransactionResponseDTO {
        logger.debug { "starting transaction for sagaId = ${transactionRequestDTO.sagaId}" }

        val sagaTransactionEntity = converterService.getStartTransactionFromDto(transactionRequestDTO)
        val parentTransaction =
            sagaTransactionService.findByIdOrThrowException(transactionRequestDTO.parentTransactionId)
        sagaTransactionEntity.parentTransaction = parentTransaction

        val savedSagaTransactionEntity = sagaTransactionService.save(sagaTransactionEntity)
        val savedStartTransactionLog =
            transactionLogService.saveStartTransactionLog(savedSagaTransactionEntity, transactionRequestDTO)

        return TransactionResponseDTO(
            savedSagaTransactionEntity.transactionId,
            transactionRequestDTO.sagaId,
            savedSagaTransactionEntity.transactionIndex,
            savedStartTransactionLog.transactionLogId
        )
    }

    @Transactional
    override fun completeTransaction(endTransaction: EndTransactionRequestDTO): TransactionResponseDTO {
        logger.debug { "Completing transaction for sagaId = ${endTransaction.sagaId} and transactionId = ${endTransaction.transactionId}" }

        val transactionEntity = sagaTransactionService.findByIdOrThrowException(endTransaction.transactionId)
        val saveCompletedTransactionLog =
            transactionLogService.saveCompletedTransactionLog(endTransaction, transactionEntity)

        //property completeSaga will be true in 2 cases
        //1 case: when saga is started
        //2 case: when saga is completing

        // in other cases complete saga always should be false
        if (endTransaction.completeSaga) {
            completeSaga(endTransaction)
        }

        sagaTransactionService.updateTransactionStatusAndPayload(
            TransactionStatus.valueOf(endTransaction.transactionStatus),
            transactionEntity.transactionId,
            endTransaction.compensationPayload
        )

        return TransactionResponseDTO(
            transactionEntity.transactionId, endTransaction.sagaId,
            transactionEntity.transactionIndex, saveCompletedTransactionLog.transactionLogId
        )
    }

    private fun completeSaga(endTransaction: EndTransactionRequestDTO) {
        transactionLogService.saveInitialOrCompletedTransaction(
            endTransaction.sagaId,
            LogStatus.SUCCESS,
            LogState.END_SAGA
        )

        //delete row from fired saga info
        sagaService.deleteFromFiredSagaById(endTransaction.sagaId)
    }

    override fun successCallback(transactionId: String) {
        TODO("Not yet implemented")
    }

    override fun compensateTransaction(compensationTransactionDTO: CompensationTransactionDTO) {
        val transactionId = compensationTransactionDTO.transactionId
        val sagaId = compensationTransactionDTO.sagaId

        logger.info {
            "***Compensating transaction = $transactionId and sagaId = $sagaId***"
        }

        //fixme find with recursive sql request
        //here should be one select
        val failedTransaction = sagaTransactionService.findByIdWithFiredSagaThrowException(transactionId, sagaId)

        handleException({
            compensate(transactionId, compensationTransactionDTO, sagaId, failedTransaction)
        }, {
            sagaService.deleteFromFiredAndSaveToErrorSaga(failedTransaction.sagaEntity)
        })
    }

    private fun compensate(
        transactionId: String,
        compensationTransactionDTO: CompensationTransactionDTO,
        sagaId: String,
        firedSagaTransactionModel: FiredSagaTransactionModel
    ) {
        val failedTransaction = firedSagaTransactionModel.sagaTransactionEntity
        val firedSagaEntity = firedSagaTransactionModel.sagaEntity

        compensationService.beforeCompensation(compensationTransactionDTO, failedTransaction, sagaId)

        val canCompleteSaga =
            compensationService.compensateTransaction(
                transactionId,
                sagaId,
                failedTransaction
            )

        if (canCompleteSaga) {
            //should be in transaction
            transactionLogService.saveInitialOrCompletedTransaction(
                compensationTransactionDTO.sagaId, LogStatus.SUCCESS, LogState.END_SAGA
            )

            sagaService.deleteFromFiredAndSaveToSuccessSaga(firedSagaEntity)
        }
    }

}