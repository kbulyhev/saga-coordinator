package ru.kmao.saga.sagacoordinator.service.converter

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Service
import ru.kmao.saga.sagacoordinator.dto.CompensationTransactionDTO
import ru.kmao.saga.sagacoordinator.entity.model.CompensationDetailModel

@Mapper(componentModel = "spring")
@Service
interface CompensationDetailConverter {
    @Mapping(source = "compensationReason", target = "compensationReason")
    @Mapping(source = "errorMessage", target = "errorMessage")
    fun getCompensationDetailFromDTO(compensationTransactionDTO: CompensationTransactionDTO): CompensationDetailModel
}