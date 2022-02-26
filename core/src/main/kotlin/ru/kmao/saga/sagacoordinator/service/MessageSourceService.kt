package ru.kmao.saga.sagacoordinator.service

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import ru.kmao.saga.sagacoordinator.exception.SagaCoordinatorErrorCodes
import java.util.Locale

@Component
class MessageSourceService(
    private val messageSource: MessageSource
) {
    fun getErrorMessage(sagaCoordinatorErrorCodes: SagaCoordinatorErrorCodes, vararg args: Any?): String {
        return messageSource.getMessage(sagaCoordinatorErrorCodes.message, args, Locale.getDefault())
    }

    fun getSagaCoordinatorExceptionMessage(errorCode: SagaCoordinatorErrorCodes, args: Array<Any>?): String {
        return messageSource.getMessage(errorCode.code, args, Locale.getDefault())
    }
}