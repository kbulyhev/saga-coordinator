package ru.kmao.saga.sagacoordinator.service.job

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.kmao.saga.sagacoordinator.api.CompensationService
import ru.kmao.saga.sagacoordinator.api.ErrorSagaService
import ru.kmao.saga.sagacoordinator.api.TransactionLogService
import ru.kmao.saga.sagacoordinator.entity.model.ErrorTransactionDataModel
import ru.kmao.saga.sagacoordinator.quartz.api.SagaJobService
import ru.kmao.saga.sagacoordinator.service.partitions
import javax.transaction.Transactional


@Service
class SagaJobServiceImpl(
    private val errorSagaService: ErrorSagaService,
    private val transactionLogService: TransactionLogService,
    private val compensationService: CompensationService
) : SagaJobService {

    @Value("\${spring.quartz.saga.error-job.data-limit: 100}")
    private var limit: Int = 100

    @Value("\${spring.quartz.saga.error-job.transaction-attempts-to-recover: 5}")
    private var attemptsToRecover: Int = 5

    private val logger = KotlinLogging.logger {}

    override fun executeErrorSagaJob() {

        logger.debug { "executing error saga job" }

        val countOfElements = errorSagaService.countByAttemptsToRecover(attemptsToRecover)

        logger.debug { "found $countOfElements records with error saga where attemptsToRecover = $attemptsToRecover in database, start to process it" }

        if (countOfElements <= 1000) {
            logger.debug { "Process data in memory..." }
            processDataInMemory()
        }

//        errorSagaService.getAllErrorSagaIds(limit)
    }

    private fun processDataInMemory() {
        val sagaIdsPartitions = errorSagaService.findAllIds(attemptsToRecover).partitions(50)

        runBlocking {

            sagaIdsPartitions.map {
                launch {
                    logger.debug { "launch coroutine to process list of sagaIds = $it" }
                    processListOfSagaIds(it)
                }
            }.forEach { it.join() }
        }
    }

    private suspend fun processListOfSagaIds(sagaIds: Set<String>) {
        //n+1 query
        val transactionLogMap = transactionLogService.findLastErrorTransactionLog(sagaIds).associateBy { it.sagaId }

        coroutineScope {

            for (entry in transactionLogMap) {
                val sagaId = entry.key
                val transactionLogEntity = entry.value

                if (sagaIds.contains(sagaId)) {
                    launch {
                        processErrorTransaction(transactionLogEntity)
                    }
                } else {
                    errorSagaService.deleteById(sagaId)
                }
            }

        }
    }

    private suspend fun processErrorTransaction(errorTransactionDataModel: ErrorTransactionDataModel) {

        try {
            compensationService.compensateTransactionFromJob(errorTransactionDataModel)
        } catch (exc: Exception) {
            saveErrorAttemptToCompensate(errorTransactionDataModel)
        }
    }

    //todo: check that transaction is really work here
    @Transactional
    fun saveErrorAttemptToCompensate(errorTransactionDataModel: ErrorTransactionDataModel) {

        //save log about unsuccessfull compensation transaction
        with(errorTransactionDataModel) {
            transactionLogService.saveCompensationTransactionLogNative(
                transactionId = transactionId,
                transactionLogName = transactionLogName,
                sagaId = sagaId,
                state = state
            )
        }

        //and update error saga attempt
        errorSagaService.updateAttemptsToRecover(errorTransactionDataModel.sagaId)
    }
}