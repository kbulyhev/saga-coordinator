package ru.kmao.saga.sagacoordinator.constant

enum class LogState {
    START_SAGA, START_TRANSACTION, END_TRANSACTION, END_SAGA, COMPENSATION_TRANSACTION, FAILED_TRANSACTION
}

enum class LogStatus {
    SUCCESS, ERROR
}

enum class TransactionStatus {
    CREATED, COMPLETED, FAILED
}

enum class SagaStatus {
    EXECUTING, ERROR
}