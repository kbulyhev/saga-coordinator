package ru.kmao.saga.sagacoordinator.api

import ru.kmao.saga.sagacoordinator.entity.TransactionLogEntity
import ru.kmao.saga.sagacoordinator.entity.model.CompensationDetailModel

interface CompensationDetailService {
    fun saveCompensationDetails(
        compensationDetails: MutableList<CompensationDetailModel>,
        transactionLogEntity: TransactionLogEntity
    )

    fun saveCompensationDetails(
        compensationDetailModel: CompensationDetailModel,
        transactionLogEntity: TransactionLogEntity
    )
}