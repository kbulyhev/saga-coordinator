package ru.kmao.saga.sagacoordinator.dto

import javax.validation.constraints.NotNull

data class StartSagaRequestDTO(

    @field:NotNull
    val serviceName: String,

    @field:NotNull
    val compensationUrl: String
)