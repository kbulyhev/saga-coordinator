package ru.kmao.saga.sagacoordinator.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.kmao.saga.sagacoordinator.api.SagaCoordinatorService
import ru.kmao.saga.sagacoordinator.dto.CompensationTransactionDTO
import ru.kmao.saga.sagacoordinator.dto.EndTransactionRequestDTO
import ru.kmao.saga.sagacoordinator.dto.StartSagaRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionRequestDTO
import ru.kmao.saga.sagacoordinator.dto.TransactionResponseDTO
import javax.validation.Valid

@RestController
@RequestMapping("/saga")
class SagaTransactionController(
    private val sagaCoordinatorService: SagaCoordinatorService
) {

    @PostMapping("/start")
    fun createSaga(@Valid @RequestBody transactionRequestDTO: StartSagaRequestDTO): TransactionResponseDTO {

        return sagaCoordinatorService.createSaga(transactionRequestDTO)
    }

//    @PostMapping("/complete")
//    fun completeSaga(@Valid @RequestBody completeSagaDTO: CompleteSagaDTO) {
//
//        sagaCoordinatorService.completeSaga(completeSagaDTO)
//    }

    @PostMapping("/transaction/start")
    fun startTransaction(@Valid @RequestBody transactionRequestDTO: TransactionRequestDTO): TransactionResponseDTO {

        return sagaCoordinatorService.startTransaction(transactionRequestDTO)
    }

    @PostMapping("/transaction/complete")
    fun complete(@Valid @RequestBody endTransaction: EndTransactionRequestDTO): TransactionResponseDTO {

        return sagaCoordinatorService.completeTransaction(endTransaction)
    }

    @PostMapping("/transaction/compensate")
    fun compensateTransaction(@Valid @RequestBody compensationTransactionDTO: CompensationTransactionDTO) {
        sagaCoordinatorService.compensateTransaction(compensationTransactionDTO)
    }

    @PostMapping("/success-callback/{transactionId}")
    fun successCallback(@PathVariable transactionId: String) {
        sagaCoordinatorService.successCallback(transactionId)
    }
}