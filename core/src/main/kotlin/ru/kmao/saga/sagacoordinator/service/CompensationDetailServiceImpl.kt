package ru.kmao.saga.sagacoordinator.service

import org.springframework.stereotype.Service
import ru.kmao.saga.sagacoordinator.api.CompensationDetailService
import ru.kmao.saga.sagacoordinator.entity.CompensationDetailEntity
import ru.kmao.saga.sagacoordinator.entity.TransactionLogEntity
import ru.kmao.saga.sagacoordinator.entity.model.CompensationDetailModel
import ru.kmao.saga.sagacoordinator.repository.CompensationDetailRepository

@Service
class CompensationDetailServiceImpl(
    private val compensationDetailRepository: CompensationDetailRepository
) : CompensationDetailService {

    override fun saveCompensationDetails(
        compensationDetails: MutableList<CompensationDetailModel>,
        transactionLogEntity: TransactionLogEntity
    ) {
        if (compensationDetails.size > 1) {
            val entities: List<CompensationDetailEntity> = compensationDetails.map {
                getCompensationDetailEntity(it, transactionLogEntity)
            }
            compensationDetailRepository.saveAll(entities)
        } else {
            val compensationDetailModel = compensationDetails.first()

            saveCompensationDetails(compensationDetailModel, transactionLogEntity)
        }

        compensationDetails.clear()
    }

    override fun saveCompensationDetails(
        compensationDetailModel: CompensationDetailModel,
        transactionLogEntity: TransactionLogEntity
    ) {
        val compensationDetailEntity = getCompensationDetailEntity(compensationDetailModel, transactionLogEntity)
        compensationDetailRepository.save(compensationDetailEntity)
    }

    private fun getCompensationDetailEntity(
        it: CompensationDetailModel,
        transactionLogEntity: TransactionLogEntity
    ): CompensationDetailEntity {
        return with(it) {
            CompensationDetailEntity(
                null,
                transactionLog = transactionLogEntity,
                compensationReason = compensationReason,
                errorMessage = errorMessage
            )
        }
    }

}