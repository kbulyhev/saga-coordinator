package ru.kmao.saga.sagacoordinator.exception

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.kmao.saga.sagacoordinator.service.MessageSourceService
import java.time.OffsetDateTime

@RestControllerAdvice
class SagaTransactionControllerAdvice {

    @Autowired
    private lateinit var messageSourceService: MessageSourceService

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(e: MethodArgumentNotValidException): List<ErrorResponse> {
        return e.bindingResult.fieldErrors
            .map { fieldError: FieldError ->
                val errorMessage = messageSourceService.getErrorMessage(
                    SagaCoordinatorErrorCodes.FIELD_ERROR,
                    fieldError.field,
                    fieldError.defaultMessage
                )

                ErrorResponse.getValidationResponse(SagaCoordinatorErrorCodes.FIELD_ERROR, errorMessage, e)
            }
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpNotReadableException(e: HttpMessageNotReadableException): ErrorResponse {

        return ErrorResponse.getExceptionResponse(e)
    }

    @ExceptionHandler(value = [SagaCoordinatorBaseException::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleSagaCoordinatorException(e: SagaCoordinatorBaseException): ErrorResponse {

        e.errorCode?.let {
            val errorMessage = messageSourceService.getSagaCoordinatorExceptionMessage(it, e.args)
            return ErrorResponse(OffsetDateTime.now(), it.code, errorMessage, e.javaClass.simpleName, e.description)
        }

        e.description?.let {
            return ErrorResponse(OffsetDateTime.now(), null, e.message, e.javaClass.simpleName, e.description)
        }

        return ErrorResponse.getExceptionResponse(e)
    }

    @ExceptionHandler(value = [Exception::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: HttpMessageNotReadableException): ErrorResponse = ErrorResponse.getExceptionResponse(e)
}