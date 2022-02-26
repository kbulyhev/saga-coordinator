package ru.kmao.saga.sagacoordinator.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.kmao.saga.sagacoordinator.constant.LogStatus
import ru.kmao.saga.sagacoordinator.entity.TransactionLogEntity
import ru.kmao.saga.sagacoordinator.entity.model.ErrorTransactionDataModel
import java.util.Optional

@Repository
interface TransactionLogRepository : JpaRepository<TransactionLogEntity, Long> {
    fun findBySagaIdAndName(sagaId: String, name: String): TransactionLogEntity?

    @Modifying
    @Query("update TransactionLogEntity tr set tr.status = ?2 where tr.transactionLogId = ?1")
    fun updateLogStatusById(transactionLogId: Long, logStatus: LogStatus)

    fun existsByNameAndSagaId(sagaId: String, name: String): Boolean

    @Query(
//        "select ru.kmao.saga.sagacoordinator.entity.model.ErrorTransactionDataModel from TransactionLogEntity t " +
//                "inner join SagaTransactionEntity st on st.transactionId = t.transaction where t.status = :logStatus " +
//                "and t.sagaId in (:sagaIds)" +
//                "" +
        "select ru.kmao.saga.sagacoordinator.entity.model.ErrorTransactionDataModel " +
                "from TransactionLogEntity tl " +
                "         inner join SagaTransactionEntity st on st.transactionId = tl.transaction " +
                "where tl.transactionLogId in (" +
                "    select max(tl.transactionLogId) " +
                "    from TransactionLogEntity itl" +
                "    where itl.status = :logStatus" +
                "      and itl.sagaId in (:sagaIds) " +
                "    group by itl.sagaId )"
    )
    fun findLastErrorTransactionLog(
        @Param("sagaIds") sagaIds: Set<String>,
        @Param("logStatus") logStatus: LogStatus
    ): List<ErrorTransactionDataModel>

    @Query(
        value = "insert into transaction_log(transaction_log_id, transaction_id, saga_id, name, state, status, timestamp) " +
                "values (nextval('transaction_log_seq'), :transactionId, :sagaId, :transactionLogName, :state, :logStatus, now())",
        nativeQuery = true
    )
    fun saveNative(
        @Param("transactionId") transactionId: String,
        @Param("sagaId") sagaId: String,
        @Param("transactionLogName") transactionLogName: String,
        @Param("state") state: String,
        @Param("logStatus") logStatus: LogStatus
    ): TransactionLogEntity
}