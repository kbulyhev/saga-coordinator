package ru.kmao.saga.sagacoordinator.service.converter

import org.springframework.stereotype.Service
import ru.kmao.saga.sagacoordinator.api.converter.SagaTransactionConverterService
import ru.kmao.saga.sagacoordinator.constant.TransactionStatus
import ru.kmao.saga.sagacoordinator.dto.StartSagaRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionRequestDTO
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity
import java.time.OffsetDateTime
import java.util.UUID

@Service
class SagaTransactionConverterServiceImpl : SagaTransactionConverterService {
    override fun getStartSagaTransactionFromDto(
        transactionRequestDTO: StartSagaRequestDTO,
        sagaId: String?
    ): SagaTransactionEntity {
        return with(transactionRequestDTO) {
            SagaTransactionEntity(
                null,
                sagaId!!,
                serviceName = serviceName,
                transactionIndex = "1",
                transactionStatus = TransactionStatus.CREATED,
                timestamp = OffsetDateTime.now(),
                requestPayload = null,
                null,
                requestUrl = null,
                compensationUrl = compensationUrl,
                compensated = false
            )
        }
    }

    override fun getStartTransactionFromDto(transactionRequestDTO: TransactionRequestDTO): SagaTransactionEntity {
        return with(transactionRequestDTO) {
            SagaTransactionEntity(
                transactionId = UUID.randomUUID().toString(),
                sagaId = sagaId,
                serviceName = serviceName,
                transactionIndex = transactionIndex,
                transactionStatus = TransactionStatus.CREATED,
                timestamp = OffsetDateTime.now(),
                requestPayload = requestPayload,
                null,
                requestUrl = requestUrl,
                compensationUrl = compensationUrl,
                compensated = false
            )
        }
    }
}