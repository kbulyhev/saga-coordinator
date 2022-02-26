package ru.kmao.saga.sagacoordinator.dto

data class TransactionResponseDTO(
    val transactionId: String?,
    val sagaId: String,
    val sagaIndex: String,
    val transactionLogId: Long?,
)