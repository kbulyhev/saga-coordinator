package ru.kmao.saga.sagacoordinator.exception

import java.time.OffsetDateTime

data class ErrorResponse(
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    val innerErrorCode: String?,
    val message: String?,
    val exceptionName: String,
    val description: String?

) {

    companion object Response {
        fun getValidationResponse(
            errorCode: SagaCoordinatorErrorCodes,
            errorMessage: String,
            e: Throwable
        ): ErrorResponse =
            ErrorResponse(OffsetDateTime.now(), errorCode.code, errorMessage, e.javaClass.simpleName, null)

        fun getExceptionResponse(e: Throwable): ErrorResponse =
            ErrorResponse(OffsetDateTime.now(), null, e.message, e.javaClass.simpleName, null)
    }
}