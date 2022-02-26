package ru.kmao.saga.sagacoordinator.dto

import javax.validation.constraints.NotNull

data class TransactionRequestDTO(

    @field:NotNull
    val sagaId: String,

    @field:NotNull
    val parentTransactionId: String,

    @field:NotNull
    val serviceName: String,

    @field:NotNull
    val transactionIndex: String,

    @field:NotNull
    val requestPayload: String,

    val compensationPayload: String?,

    val requestUrl: String?,

    val compensationUrl: String?,

    @field:NotNull
    val logStatus: String,
)