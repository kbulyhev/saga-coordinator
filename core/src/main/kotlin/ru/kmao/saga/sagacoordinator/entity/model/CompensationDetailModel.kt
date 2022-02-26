package ru.kmao.saga.sagacoordinator.entity.model

data class CompensationDetailModel(

    var compensationReason: String,

    var errorMessage: String? = null
)