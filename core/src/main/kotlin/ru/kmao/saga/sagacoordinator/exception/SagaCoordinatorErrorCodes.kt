package ru.kmao.saga.sagacoordinator.exception

enum class SagaCoordinatorErrorCodes(
    val code: String,
    val message: String
) {
    FIELD_ERROR("000", "field.error"),
    SERVICE_COMPENSATION_ERROR("001", "service.compensate.error"),
    REQUIRED_FIELD("002", "property.required")
}