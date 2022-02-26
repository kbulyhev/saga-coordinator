package ru.kmao.saga.sagacoordinator.entity

import org.hibernate.annotations.GenericGenerator
import ru.kmao.saga.sagacoordinator.constant.SagaStatus
import java.time.OffsetDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "fired_saga")
class FiredSagaEntity(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "saga_id", nullable = false)
    val sagaId: String? = null,

    @Column(name = "saga_status", length = 128, nullable = false)
    @Enumerated(value = EnumType.STRING)
    val sagaStatus: SagaStatus = SagaStatus.EXECUTING,

    @Column(name = "fired_time", nullable = false)
    val firedTime: OffsetDateTime = OffsetDateTime.now()
)

@Entity
@Table(name = "error_saga")
class ErrorSagaEntity(
    @Id
    @Column(name = "saga_id", nullable = false)
    val sagaId: String?,

    @Column(name = "fired_time", nullable = false)
    val firedTime: OffsetDateTime,

    @Column(name = "error_time", nullable = false)
    val errorTime: OffsetDateTime,

    @Column(name = "attempts_to_recover", nullable = false)
    val attemptsToRecover: Int = 0
)

@Entity
@Table(name = "success_saga")
class SuccessSagaEntity(
    @Id
    @Column(name = "saga_id", nullable = false)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    val sagaId: String? = null,

    @Column(name = "fired_time", nullable = false)
    val firedTime: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "finish_time", nullable = false)
    val finishTime: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "attempts_to_recover", nullable = false)
    val attemptsToRecover: Int = 0,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "successSaga")
    val transactions: Set<SagaTransactionEntity> = setOf(),
)