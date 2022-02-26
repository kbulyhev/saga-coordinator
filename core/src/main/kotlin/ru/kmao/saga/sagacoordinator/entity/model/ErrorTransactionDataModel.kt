package ru.kmao.saga.sagacoordinator.entity.model

data class ErrorTransactionDataModel (
    val transactionId: String,
    val compensationPayload: String,
    val compensationUrl: String,
    val sagaId: String,
    val transactionLogName: String,
    val state: String,
    val parentTransactionId: String?
) {
//    constructor(transactionId: String, )
}