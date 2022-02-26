package ru.kmao.saga.sagacoordinator.entity

import ru.kmao.saga.sagacoordinator.constant.LogState
import ru.kmao.saga.sagacoordinator.constant.LogStatus
import java.time.OffsetDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "transaction_log")
class TransactionLogEntity(
    @Id
    @Column(name = "transaction_log_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_generator")
    @SequenceGenerator(name = "seq_generator", sequenceName = "transaction_log_seq", allocationSize = 1)
    var transactionLogId: Long? = null,

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    var transaction: SagaTransactionEntity? = null,

    @Column(name = "saga_id", nullable = false)
    var sagaId: String,

    @Column(name = "name", nullable = false, length = 128)
    var name: String,

    @Column(name = "state", nullable = false, length = 50)
    @Enumerated(value = EnumType.STRING)
    var state: LogState,

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(value = EnumType.STRING)
    var status: LogStatus,

    @Column(name = "\"timestamp\"", nullable = false)
    val timestamp: OffsetDateTime
)