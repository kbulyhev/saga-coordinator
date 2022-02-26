package ru.kmao.saga.sagacoordinator.entity

import org.hibernate.annotations.GenericGenerator
import ru.kmao.saga.sagacoordinator.constant.TransactionStatus
import java.time.OffsetDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "saga_transaction")
class SagaTransactionEntity(
    @Id
    @Column(name = "transaction_id", nullable = false)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    var transactionId: String? = null,

    @Column(name = "sagaId", nullable = false)
    var sagaId: String,

    @Column(name = "service_name", nullable = false, length = 128)
    var serviceName: String,

    @Column(name = "transaction_index", nullable = false, length = 128)
    var transactionIndex: String,

    @Column(name = "transaction_status", nullable = false, length = 128)
    @Enumerated(value = EnumType.STRING)
    var transactionStatus: TransactionStatus,

    @Column(name = "\"timestamp\"", nullable = false)
    var timestamp: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "request_payload")
    var requestPayload: String? = null,

    @Column(name = "compensation_payload")
    var compensationPayload: String? = null,

    @Column(name = "request_url")
    var requestUrl: String? = null,

    @Column(name = "compensation_url")
    var compensationUrl: String? = null,

    @Column(name = "compensated", nullable = false)
    var compensated: Boolean = false,

//    @Column(name = "current_transaction")
//    var currentTransaction: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_transaction_id")
    var parentTransaction: SagaTransactionEntity? = null,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "parentTransaction")
    val children: Set<SagaTransactionEntity> = setOf(),

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "transaction")
    val transactionLog: Set<TransactionLogEntity> = setOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saga_id")
    val successSaga: SuccessSagaEntity? = null,
)