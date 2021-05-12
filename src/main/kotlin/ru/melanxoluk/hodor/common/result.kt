package ru.melanxoluk.hodor.common


fun <T : Any> result(isOk: Boolean, value: () -> T, err: () -> Throwable) = when {
    isOk -> Result.success(value())
    else -> Result.failure(err())
}

fun <T : Any> result(value: T?, err: () -> Throwable): Result<T> {
    if (value == null)
        return Result.failure(err())

    return Result.success(value)
}

fun <T : Any> msgResult(value: T?, msg: () -> String): Result<T> {
    return result(value) { IllegalArgumentException(msg()) }
}

fun <T:Any> notFoundResult(value: T?): Result<T> {
    return result(value) { IllegalArgumentException("Not found") }
}

fun <R: Any, T: Any> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return when {
        isSuccess -> transform(getOrThrow())
        else -> Result.failure(exceptionOrNull()!!)
    }
}

