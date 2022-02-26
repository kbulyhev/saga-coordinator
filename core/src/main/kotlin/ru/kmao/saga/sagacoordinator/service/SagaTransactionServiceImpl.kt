package ru.kmao.saga.sagacoordinator.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kmao.saga.sagacoordinator.api.SagaTransactionService
import ru.kmao.saga.sagacoordinator.constant.TransactionStatus
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity
import ru.kmao.saga.sagacoordinator.entity.model.FiredSagaTransactionModel
import ru.kmao.saga.sagacoordinator.exception.SagaCoordinatorBaseException
import ru.kmao.saga.sagacoordinator.repository.SagaTransactionRepository

@Service
class SagaTransactionServiceImpl : SagaTransactionService {

    @Autowired
    private lateinit var sagaTransactionRepository: SagaTransactionRepository

    override fun save(startTransactionEntity: SagaTransactionEntity): SagaTransactionEntity {
        return sagaTransactionRepository.save(startTransactionEntity)
    }

    override fun findByIdOrThrowException(transactionId: String): SagaTransactionEntity {
        return sagaTransactionRepository.findById(transactionId).orElseThrow {
            SagaCoordinatorBaseException("There is no such transaction by transactionId = $transactionId")
        }
    }

    override fun findByIdWithFiredSagaThrowException(
        transactionId: String,
        sagaId: String
    ): FiredSagaTransactionModel {
        return sagaTransactionRepository.findByIdWithFiredSaga(transactionId).orElseThrow {
            SagaCoordinatorBaseException("There is no such transaction by transactionId = $transactionId and fired saga")
        }
    }

    override fun updateTransactionStatus(transactionStatus: TransactionStatus, transactionId: String?) {
        sagaTransactionRepository.updateTransactionStatus(
            transactionStatus,
            transactionId
        )
    }

    override fun updateCompensationPayload(compensationPayload: String, transactionId: String) {
        sagaTransactionRepository.updateCompensationPayload(compensationPayload, transactionId)
    }

    override fun updateTransactionStatusAndPayload(
        transactionStatus: TransactionStatus,
        transactionId: String?,
        compensationPayload: String
    ) {
        sagaTransactionRepository.updateTransactionStatusAndCompensationPayload(
            transactionStatus,
            compensationPayload,
            transactionId
        )
    }

    override fun updateTransactionStatusAndCompensated(
        transactionStatus: TransactionStatus,
        compensated: Boolean,
        transactionId: String?
    ) {
        sagaTransactionRepository.updateTransactionStatusAndCompensated(
            transactionStatus,
            compensated,
            transactionId
        )
    }


    override fun updateCompensated(compensated: Boolean, transactionId: String?) {
        sagaTransactionRepository.updateCompensated(compensated, transactionId)
    }

}