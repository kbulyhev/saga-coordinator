package ru.kmao.saga.sagacoordinator.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.kmao.saga.sagacoordinator.constant.TransactionStatus
import ru.kmao.saga.sagacoordinator.entity.SagaTransactionEntity
import ru.kmao.saga.sagacoordinator.entity.model.FiredSagaTransactionModel
import java.util.Optional

interface SagaTransactionRepository : JpaRepository<SagaTransactionEntity, String> {

    @Modifying
    @Query("update SagaTransactionEntity tr set tr.transactionStatus = ?1, tr.compensationPayload = ?2 where tr.transactionId = ?3")
    fun updateTransactionStatusAndCompensationPayload(
        @Param("transactionStatus") transactionStatus: TransactionStatus,
        @Param("compensationPayload") compensationPayload: String,
        @Param("transactionId") transactionId: String?
    )

    @Modifying
    @Query("update SagaTransactionEntity tr set tr.transactionStatus = ?1 where tr.transactionId = ?2")
    fun updateTransactionStatus(completed: TransactionStatus, transactionId: String?)

    @Modifying
    @Query("update SagaTransactionEntity tr set tr.transactionStatus = ?1, tr.compensated = ?2 where tr.transactionId = ?3")
    fun updateTransactionStatusAndCompensated(
        @Param("transactionStatus") transactionStatus: TransactionStatus,
        @Param("compensated") compensated: Boolean,
        @Param("transactionId") transactionId: String?
    )

    @Modifying
    @Query("update SagaTransactionEntity tr set  tr.compensated = ?1 where tr.transactionId = ?2")
    fun updateCompensated(
        @Param("compensated") compensated: Boolean,
        @Param("transactionId") transactionId: String?
    )

    @Modifying
    @Query("update SagaTransactionEntity tr set  tr.compensationPayload = ?1 where tr.transactionId = ?2")
    fun updateCompensationPayload(compensationPayload: String, transactionId: String)

    @EntityGraph(
        type = EntityGraph.EntityGraphType.FETCH,
        attributePaths = ["saga"]
    )
    @Query(
        "select ru.kmao.saga.sagacoordinator.entity.model.FiredSagaTransactionModel from SagaTransactionEntity st " +
            "inner join FiredSagaEntity fs where st.sagaId = fs.sagaId")
    fun findByIdWithFiredSaga(@Param("transactionId") transactionId: String): Optional<FiredSagaTransactionModel>
}