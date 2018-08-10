package com.melanxoluk.hodor.services

import org.koin.standalone.KoinComponent


// if isOk
//   errorMessage = null
data class UnitServiceResult(val isOk: Boolean = false,
                             @Transient val isClientError: Boolean = false,
                             @Transient val isServerError: Boolean = false,
                             val errorMessage: String? = null) {
    companion object {
        private val OK = UnitServiceResult(true, false, false, null)

        fun serverError(msg: String) = UnitServiceResult(false, false, true, msg)
        fun clientError(msg: String) = UnitServiceResult(true, true, false, msg)
        fun ok() = OK
    }
}

// if isOk
//   result != null
//   errorMessage = null
data class ServiceResult<T>(val isOk: Boolean = false,
                            @Transient val isClientError: Boolean = false,
                            @Transient val isServerError: Boolean = false,
                            val errorMessage: String? = null,
                            val result: T? = null) {

    val isError get() = !isOk

    companion object {
        fun <T> serverError(msg: String) = ServiceResult<T>(false, false, true, msg, null)
        fun <T> clientError(msg: String) = ServiceResult<T>(false, true, false, msg, null)
        fun <T> ok(result: T?) = ServiceResult(true, result = result)
    }
}

interface Service: KoinComponent {
    fun <T> ok(action: () -> T?): ServiceResult<T> {
        return try {
            val res = action()
            if (res is ServiceResult<*>)
                return res as ServiceResult<T>
            ServiceResult.ok(res)
        } catch (t: Throwable) {
            ServiceResult.serverError(t.message ?: t.javaClass.name)
        }
    }

    fun <T> clientError(message: String): ServiceResult<T> {
        return ServiceResult.clientError(message)
    }

    fun <T> serverError(message: String): ServiceResult<T> {
        return ServiceResult.serverError(message)
    }


    fun unitOk(action: () -> Unit): UnitServiceResult {
        return try {
            UnitServiceResult.ok()
        } catch (t: Throwable) {
            UnitServiceResult.serverError(t.message ?: t.javaClass.name)
        }
    }

    fun unitClientError(message: String): UnitServiceResult {
        return UnitServiceResult.clientError(message)
    }

    fun serverClientError(message: String): UnitServiceResult {
        return UnitServiceResult.serverError(message)
    }
}