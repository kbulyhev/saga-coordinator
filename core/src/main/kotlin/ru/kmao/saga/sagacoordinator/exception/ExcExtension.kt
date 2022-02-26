package ru.kmao.saga.sagacoordinator.exception


inline fun <T> checkNotNullOrThrowException(
    value: T?,
    args: Array<Any>?,
    sagaCoordinatorErrorCodes: () -> SagaCoordinatorErrorCodes
): T {

    if (value == null) {
        val errorCode = sagaCoordinatorErrorCodes()
        throw SagaCoordinatorBaseException(errorCode, args)
    } else {
        return value
    }
}
