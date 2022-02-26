package ru.kmao.saga.sagacoordinator.service

import org.springframework.stereotype.Service
import ru.kmao.saga.sagacoordinator.api.ErrorSagaService
import ru.kmao.saga.sagacoordinator.entity.ErrorSagaEntity
import ru.kmao.saga.sagacoordinator.repository.ErrorSagaRepository

@Service
class ErrorSagaServiceImpl(
    private val errorSagaRepository: ErrorSagaRepository
) : ErrorSagaService {

    override fun countByAttemptsToRecover(attemptsToRecover: Int): Int {
        return errorSagaRepository.countByAttemptsToRecoverLessThanEqual(attemptsToRecover)
    }

    override fun save(errorSagaService: ErrorSagaEntity): ErrorSagaEntity {
        return errorSagaRepository.save(errorSagaService)
    }

    override fun deleteById(sagaId: String) {
        errorSagaRepository.deleteById(sagaId)
    }

    override fun updateAttemptsToRecover(sagaId: String) {
        errorSagaRepository.updateAttemptsToRecover(sagaId)
    }

    override fun findAllIds(attemptsToRecover: Int): List<String> {
        return errorSagaRepository.findAllIdsByAttemptsToRecover(attemptsToRecover)
    }
}