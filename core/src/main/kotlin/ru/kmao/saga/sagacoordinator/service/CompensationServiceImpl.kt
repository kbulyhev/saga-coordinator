package ru.kmao.saga.sagacoordinator.service

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import ru.kmao.saga.sagacoordinator.api.CompensationDetailService
import ru.kmao.saga.sagacoordinator.api.CompensationService
import ru.kmao.saga.sagacoordinator.api.RestTemplateService
import ru.kmao.saga.sagacoordinator.api.SagaTransactionService
import ru.kmao.saga.sagacoordinator.api.TransactionLogService
import ru.kmao.saga.sagacoordinator.service.converter.CompensationDetailConverter
import ru.kmao.saga.sagacoordinator.constant.LogState
import ru.kmao.saga.sagacoordinator.constant.LogStatus
import ru.kmao.saga.sagacoordinator.constant.TransactionStatus
import ru.kmao.saga.sagacoordinator.dto.CompensationTransactionDTO
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity
import ru.kmao.saga.sagacoordinator.entity.TransactionLogEntity
import ru.kmao.saga.sagacoordinator.entity.model.CompensationDetailModel
import ru.kmao.saga.sagacoordinator.entity.model.ErrorTransactionDataModel
import ru.kmao.saga.sagacoordinator.exception.SagaCoordinatorBaseException
import ru.kmao.saga.sagacoordinator.exception.SagaCoordinatorErrorCodes
import ru.kmao.saga.sagacoordinator.exception.checkNotNullOrThrowException
import ru.kmao.saga.sagacoordinator.utils.TransactionLogUtils
import javax.transaction.Transactional

@Service
class CompensationServiceImpl(
    private val sagaTransactionService: SagaTransactionService,
    private val compensationDetailConverter: CompensationDetailConverter,
    private val restTemplateService: RestTemplateService,
    private val compensationDetailService: CompensationDetailService,
    private val transactionLogService: TransactionLogService,
    private val transactionManager: PlatformTransactionManager,
    private var transactionTemplate: TransactionTemplate = TransactionTemplate(transactionManager)
) : CompensationService {

    private val logger = KotlinLogging.logger {}

    override fun compensateTransaction(
        transactionId: String,
        sagaId: String,
        failedTransaction: SagaTransactionEntity
    ): Boolean {

        val allTransactionsToCompensate = mutableListOf<SagaTransactionEntity>()

        failedTransaction.compensationPayload?.let {
            allTransactionsToCompensate.add(failedTransaction)
        }

        getAllTransactionsToCompensate(failedTransaction, allTransactionsToCompensate)

        val compensationDetails = mutableListOf<CompensationDetailModel>()

        allTransactionsToCompensate.forEachIndexed { _, element ->

            try {
                compensateTransactionAndReturnLogStatus(element.compensationUrl, element.compensationPayload)
            } catch (exc: Exception) {
                executeFailedCompensationTransaction(element, sagaId, compensationDetails)
                throw exc
            }

            executeSuccessCompensationTransaction(element, sagaId, compensationDetails)
        }

        return true
    }

    override fun compensateTransactionFromJob(errorTransactionDataModel: ErrorTransactionDataModel) {

        //try to compensate current transaction

        try {
            compensateTransactionAndReturnLogStatus(
                errorTransactionDataModel.compensationUrl,
                errorTransactionDataModel.compensationPayload
            )
        } catch (exc: Exception) {
            logger.error { exc }

            throw exc
        }

        errorTransactionDataModel.parentTransactionId?.let {
            val sagaTransactionEntity = sagaTransactionService.findByIdOrThrowException(it)

            //if first transaction is compensated successfully
            compensateTransaction(
                transactionId = errorTransactionDataModel.transactionId,
                sagaId = errorTransactionDataModel.sagaId,
                failedTransaction = sagaTransactionEntity
            )
        }
    }

    private fun executeFailedCompensationTransaction(
        element: SagaTransactionEntity, sagaId: String, compensationDetails: MutableList<CompensationDetailModel>
    ) {
        transactionTemplate.execute {
            val transactionLogEntity = updateTransactionStatusAndCompensatedAndSaveLog(
                element, sagaId, LogStatus.ERROR, false, TransactionStatus.FAILED
            )

            compensationDetailService.saveCompensationDetails(compensationDetails, transactionLogEntity)
        }
    }

    private fun executeSuccessCompensationTransaction(
        element: SagaTransactionEntity, sagaId: String, compensationDetails: MutableList<CompensationDetailModel>
    ) {
        transactionTemplate.execute {
            val transactionLogEntity = updateTransactionStatusAndSaveLog(element, sagaId, LogStatus.SUCCESS, true)

            if (compensationDetails.isNotEmpty()) {
                compensationDetailService.saveCompensationDetails(compensationDetails, transactionLogEntity)
            }
        }
    }

    @Transactional
    override fun beforeCompensation(
        compensationTransactionDTO: CompensationTransactionDTO, failedTransaction: SagaTransactionEntity, sagaId: String
    ) {

        if (failedTransaction.transactionStatus != TransactionStatus.FAILED && compensationTransactionDTO.compensationPayload != null) {
//            sagaTransactionService.updateTransactionStatusAndPayload(
//                TransactionStatus.FAILED,
//                failedTransaction.transactionId,
//                compensationTransactionDTO.compensationPayload!!
//            )
            failedTransaction.transactionStatus = TransactionStatus.FAILED
            failedTransaction.compensationPayload = compensationTransactionDTO.compensationPayload

        } else if (failedTransaction.transactionStatus != TransactionStatus.FAILED && compensationTransactionDTO.compensationPayload == null) {
//            sagaTransactionService.updateTransactionStatus(
//                TransactionStatus.FAILED,
//                failedTransaction.transactionId
//            )

            failedTransaction.transactionStatus = TransactionStatus.FAILED
        } else if (failedTransaction.transactionStatus == TransactionStatus.FAILED && compensationTransactionDTO.compensationPayload != null) {
//            sagaTransactionService.updateCompensationPayload(
//                compensationTransactionDTO.compensationPayload!!,
//                compensationTransactionDTO.transactionId
//            )
            failedTransaction.compensationPayload = compensationTransactionDTO.compensationPayload
        }
        sagaTransactionService.save(failedTransaction)

        val logName = TransactionLogUtils.getLogName(
            failedTransaction.serviceName, failedTransaction.transactionIndex, LogState.FAILED_TRANSACTION
        )

        val exists = transactionLogService.isTransactionLogExistsByUniqueIndex(sagaId, logName)

        if (!exists) {
            val failedTransactionLog =
                transactionLogService.saveFailedTransactionLog(failedTransaction, sagaId, logName)

            compensationDetailService.saveCompensationDetails(
                compensationDetailConverter.getCompensationDetailFromDTO(
                    compensationTransactionDTO
                ), failedTransactionLog
            )
        }

    }

    private fun updateTransactionStatusAndCompensatedAndSaveLog(
        transactionToCompensate: SagaTransactionEntity,
        sagaId: String,
        logStatus: LogStatus,
        compensated: Boolean,
        transactionStatus: TransactionStatus
    ): TransactionLogEntity {

        if (transactionToCompensate.transactionStatus != TransactionStatus.FAILED) {
            sagaTransactionService.updateTransactionStatusAndCompensated(
                transactionStatus, compensated, transactionToCompensate.transactionId
            )

        }
        return transactionLogService.saveCompensationTransactionLog(transactionToCompensate, sagaId, logStatus)
    }

    private fun updateTransactionStatusAndSaveLog(
        transactionToCompensate: SagaTransactionEntity, sagaId: String, logStatus: LogStatus, compensated: Boolean
    ): TransactionLogEntity {
        sagaTransactionService.updateCompensated(
            compensated,
            transactionToCompensate.transactionId,
        )
        return transactionLogService.saveCompensationTransactionLog(transactionToCompensate, sagaId, logStatus)
    }


    private fun compensateTransactionAndReturnLogStatus(
        compensationUrl: String?,
        compensationPayload: String?
    ) {

        val response = restTemplateService.executePost(
            getCompensationUrl(compensationUrl),
            getCompensationPayload(compensationPayload)
        )

        if (response.statusCode != HttpStatus.OK) {

            throw SagaCoordinatorBaseException(
                SagaCoordinatorErrorCodes.SERVICE_COMPENSATION_ERROR, "Full response = $response"
            )
        }
    }

    private fun getCompensationPayload(compensationPayload: String?): String {
        return checkNotNullOrThrowException(compensationPayload, arrayOf("compensationPayload")) {
            SagaCoordinatorErrorCodes.REQUIRED_FIELD
        }
    }

    private fun getCompensationUrl(compensationUrl: String?): String {
        return checkNotNullOrThrowException(compensationUrl, arrayOf("compensationUrl")) {
            SagaCoordinatorErrorCodes.REQUIRED_FIELD
        }
    }

    private fun getAllTransactionsToCompensate(
        transactionToCompensate: SagaTransactionEntity, allTransactionsToCompensate: MutableList<SagaTransactionEntity>
    ) {

        transactionToCompensate.parentTransaction?.let {
            allTransactionsToCompensate.add(it)

            if (it.compensationPayload == null) {
                return
            }

            getAllTransactionsToCompensate(it, allTransactionsToCompensate)
        }

        return
    }
}