package ru.kmao.saga.sagacoordinator.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.kmao.saga.sagacoordinator.api.ErrorSagaService
import ru.kmao.saga.sagacoordinator.api.FiredSagaService
import ru.kmao.saga.sagacoordinator.api.SagaService
import ru.kmao.saga.sagacoordinator.entity.ErrorSagaEntity
import ru.kmao.saga.sagacoordinator.entity.FiredSagaEntity
import ru.kmao.saga.sagacoordinator.entity.SuccessSagaEntity
import ru.kmao.saga.sagacoordinator.repository.SuccessSagaRepository
import java.time.OffsetDateTime

@Service
class SagaServiceImpl(
    private val errorSagaService: ErrorSagaService,
    private val firedSagaService: FiredSagaService,
    private val successSagaRepository: SuccessSagaRepository
) : SagaService {
    override fun createFiredSagaEntity(): FiredSagaEntity {
        return firedSagaService.createSagaInfo()
    }

    override fun deleteFromFiredSagaById(sagaId: String) {
        firedSagaService.deleteById(sagaId)
    }

    @Transactional
    override fun deleteFromFiredAndSaveToSuccessSaga(firedSagaEntity: FiredSagaEntity) {
        firedSagaEntity.sagaId?.let { firedSagaService.deleteById(it) }

        successSagaRepository.save(getSuccessSaga(firedSagaEntity))
    }

    @Transactional
    override fun deleteFromFiredAndSaveToErrorSaga(firedSagaEntity: FiredSagaEntity) {
        firedSagaEntity.sagaId?.let { firedSagaService.deleteById(it) }

        errorSagaService.save(getErrorSaga(firedSagaEntity))
    }

    private fun getErrorSaga(firedSagaEntity: FiredSagaEntity): ErrorSagaEntity {
        return with(firedSagaEntity) {
            ErrorSagaEntity(sagaId, firedTime, OffsetDateTime.now())
        }
    }

    private fun getSuccessSaga(firedSagaEntity: FiredSagaEntity): SuccessSagaEntity {
        return with(firedSagaEntity) {
            SuccessSagaEntity(sagaId, firedTime)
        }
    }

}