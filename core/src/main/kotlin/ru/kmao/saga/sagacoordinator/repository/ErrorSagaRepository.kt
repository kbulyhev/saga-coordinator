package ru.kmao.saga.sagacoordinator.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.kmao.saga.sagacoordinator.entity.ErrorSagaEntity
import ru.kmao.saga.sagacoordinator.entity.FiredSagaEntity
import ru.kmao.saga.sagacoordinator.entity.SuccessSagaEntity

@Repository
interface ErrorSagaRepository : JpaRepository<ErrorSagaEntity, String> {
    fun countByAttemptsToRecoverLessThanEqual(attempts: Int): Int

    @Query("select e.sagaId from ErrorSagaEntity e where e.attemptsToRecover <= ?1")
    fun findAllIdsByAttemptsToRecover(attemptsToRecover: Int): List<String>

    @Modifying
    @Query("update ErrorSagaEntity e set e.attemptsToRecover = e.attemptsToRecover + 1 where e.sagaId = ?2")
    fun updateAttemptsToRecover(sagaId: String)
}


@Repository
interface FiredSagaRepository : JpaRepository<FiredSagaEntity, String>

@Repository
interface SuccessSagaRepository : JpaRepository<SuccessSagaEntity, String>