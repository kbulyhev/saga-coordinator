package ru.kmao.saga.sagacoordinator.api.converter

import ru.kmao.saga.sagacoordinator.dto.StartSagaRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionRequestDTO
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity

interface SagaTransactionConverterService {
    fun getStartSagaTransactionFromDto(transactionRequestDTO: StartSagaRequestDTO, sagaId: String?): SagaTransactionEntity
    fun getStartTransactionFromDto(transactionRequestDTO: TransactionRequestDTO): SagaTransactionEntity
}