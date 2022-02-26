package ru.kmao.saga.sagacoordinator.utils

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.http.ResponseEntity
import ru.kmao.saga.sagacoordinator.constant.LogState
import ru.kmao.saga.sagacoordinator.entity.model.CompensationDetailModel

object TransactionLogUtils {

    fun getLogName(serviceName: String, transactionIndex: String, logState: LogState): String {
        return logState.name + "_" + transactionIndex + "($serviceName)"
    }
}

object CompensationDetailsUtils {

    fun createDetailsFromException(exc: Exception): CompensationDetailModel {

        val exceptionNameResult: MutableList<String> = ArrayList()
        val stackTraceResult: MutableList<String> = ArrayList()
        var chainedException: Throwable? = exc
        while (chainedException != null) {
            var stackTrace = ExceptionUtils.getStackTrace(chainedException)

            if (StringUtils.isNoneBlank(stackTrace)) {
                stackTrace = stackTrace.replace("'".toRegex(), StringUtils.EMPTY)
            }
            val exceptionName = chainedException.javaClass.simpleName
            stackTraceResult.add(stackTrace)
            exceptionNameResult.add(exceptionName)
            chainedException = chainedException.cause
        }

        val compensationReason = exceptionNameResult.joinToString { "->" }
        return CompensationDetailModel(compensationReason, stackTraceResult.toString())
    }

    fun createDetailsFromServiceResponse(
        response: ResponseEntity<String>,
        compensationUrl: String
    ): CompensationDetailModel {
        return CompensationDetailModel(compensationUrl + "_(${response.statusCode})", response.body)
    }
}