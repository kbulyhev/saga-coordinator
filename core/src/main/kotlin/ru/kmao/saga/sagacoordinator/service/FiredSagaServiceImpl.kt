package ru.kmao.saga.sagacoordinator.service

import org.springframework.stereotype.Service
import ru.kmao.saga.sagacoordinator.api.FiredSagaService
import ru.kmao.saga.sagacoordinator.entity.FiredSagaEntity
import ru.kmao.saga.sagacoordinator.repository.FiredSagaRepository

@Service
class FiredSagaServiceImpl(
    private val firedSagaRepository: FiredSagaRepository,
) : FiredSagaService {
    override fun createSagaInfo(): FiredSagaEntity {

        return firedSagaRepository.save(FiredSagaEntity())
    }

    override fun deleteById(sagaId: String) {
        firedSagaRepository.deleteById(sagaId)
    }
}