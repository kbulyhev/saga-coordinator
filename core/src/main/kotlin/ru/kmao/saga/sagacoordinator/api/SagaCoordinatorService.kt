package ru.kmao.saga.sagacoordinator.api

import ru.kmao.saga.sagacoordinator.dto.CompensationTransactionDTO
import ru.kmao.saga.sagacoordinator.dto.EndTransactionRequestDTO
import ru.kmao.saga.sagacoordinator.dto.StartSagaRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionResponseDTO

interface SagaCoordinatorService {
    fun createSaga(transactionRequestDTO: StartSagaRequestDTO): TransactionResponseDTO
//    fun completeSaga(completeSagaDTO: CompleteSagaDTO)
    fun startTransaction(transactionRequestDTO: TransactionRequestDTO): TransactionResponseDTO
    fun completeTransaction(endTransaction: EndTransactionRequestDTO): TransactionResponseDTO
    fun compensateTransaction(compensationTransactionDTO: CompensationTransactionDTO)
    fun successCallback(transactionId: String)
}