package ru.kmao.saga.sagacoordinator.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.kmao.saga.sagacoordinator.entity.CompensationDetailEntity

@Repository
interface CompensationDetailRepository : JpaRepository<CompensationDetailEntity, String>