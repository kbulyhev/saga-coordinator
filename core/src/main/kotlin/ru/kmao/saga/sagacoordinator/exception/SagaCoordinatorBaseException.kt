package ru.kmao.saga.sagacoordinator.exception

class SagaCoordinatorBaseException(
    val errorCode: SagaCoordinatorErrorCodes?,
    val description: String?,
    message: String?,
    cause: Throwable?,
    val args: Array<Any>?
) :
    RuntimeException(message, cause) {

    constructor(message: String?) : this(null, null, message, null, null)

    constructor(errorCode: SagaCoordinatorErrorCodes, cause: Throwable?) : this(errorCode, null, null, cause, null)

    constructor(errorCode: SagaCoordinatorErrorCodes) : this(errorCode, null, null, null, null)

    constructor(errorCode: SagaCoordinatorErrorCodes, args: Array<Any>?) : this(errorCode, null, null, null, args)

    constructor(errorCode: SagaCoordinatorErrorCodes, description: String) : this(errorCode, description, null, null, null)

}
