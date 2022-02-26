package ru.kmao.saga.sagacoordinator.dto

import javax.validation.constraints.NotNull

data class SagaSuccessCallbackDTO(
    @field:NotNull
    val transactionId: String,
    @field:NotNull
    val sagaId: String,
    @field:NotNull
    val sagaIndex: String
)