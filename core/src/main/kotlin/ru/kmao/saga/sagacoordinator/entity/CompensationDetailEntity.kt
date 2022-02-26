package ru.kmao.saga.sagacoordinator.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "compensation_details")
class CompensationDetailEntity(
    @Id
    @Column(name = "compensation_details_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_generator")
    @SequenceGenerator(name = "seq_generator", sequenceName = "compensation_details_seq", allocationSize = 1)
    var id: Long? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "transaction_log_id", nullable = false)
    var transactionLog: TransactionLogEntity,


    @Column(name = "compensation_reason", nullable = false)
    var compensationReason: String,


    @Column(name = "error_message")
    var errorMessage: String? = null
)