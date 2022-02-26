package ru.kmao.saga.sagacoordinator.service

inline fun handleException(
    function: () -> Unit,
    exceptionHandler: () -> Unit
) {
    return try {
        function()
    } catch (exc: Exception) {
        exceptionHandler()
        throw exc
    }
}

fun <E> Collection<E>.partitions(partitionSize: Int): Set<Set<E>> {

    val result = mutableSetOf<Set<E>>()

    var currentList = mutableSetOf<E>()
    this.forEach { element ->

        if (currentList.size == partitionSize) {
            result.add(currentList)
            currentList = mutableSetOf()
        }
        currentList.add(element)

    }

    if (currentList.isNotEmpty()) {
        result.add(currentList)
    }

    return result
}
