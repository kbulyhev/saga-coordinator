package ru.kmao.saga.sagacoordinator.dto

import javax.validation.constraints.NotNull

data class CompensationTransactionDTO(
    @field:NotNull
    val sagaId: String,

    @field:NotNull
    val transactionId: String,

    @field:NotNull
    val compensationReason: String,

    @field:NotNull
    val errorMessage: String,

    val compensationPayload: String?

)