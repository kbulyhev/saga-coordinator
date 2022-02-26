package ru.kmao.saga.sagacoordinator.dto

import javax.validation.constraints.NotNull

data class EndTransactionRequestDTO(

    @field:NotNull
    val transactionId: String,

    @field:NotNull
    val sagaId: String,

    val compensationPayload: String,

    @field:NotNull
    val logStatus: String,

    @field:NotNull
    val transactionStatus: String,

    val completeSaga: Boolean = false

)