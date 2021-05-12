package ru.melanxoluk.hodor.services

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ru.melanxoluk.hodor.domain.context.repositories.AppContextRepository
import ru.melanxoluk.hodor.domain.context.repositories.UserContextRepository
import ru.melanxoluk.hodor.domain.context.repositories.UsersRolesContextRepository
import ru.melanxoluk.hodor.domain.entities.repositories.*


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
data class ServiceResult<out T>(
    val isOk: Boolean = false,
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

    fun map(f: (T) -> Any): ServiceResult<*> {
        return if (isOk) {
            ok(f(result!!))
        } else {
            this
        }
    }
}

abstract class Service: KoinComponent {
    protected val defaultAppRolesRepository = get<DefaultAppRolesRepository>()
    protected val appCreatorsRepository = get<AppCreatorsRepository>()
    protected val appClientsRepository = get<AppClientsRepository>()
    protected val appRoleRepository = get<AppRolesRepository>()
    protected val appsRepository = get<AppsRepository>()

    protected val userRolesRepository = get<UserRolesRepository>()
    protected val usersRepository = get<UsersRepository>()

    protected val usersRolesContextRepository = get<UsersRolesContextRepository>()
    protected val userContextRepository = get<UserContextRepository>()
    protected val appContextRepository = get<AppContextRepository>()

    fun <T> ok(action: () -> Any): ServiceResult<T> {
        return try {
            val res = action()
            if (res is ServiceResult<*>)
                return res as ServiceResult<T>
            ServiceResult.ok(res as T)
        } catch (t: Throwable) {
            ServiceResult.serverError(t.message ?: t.javaClass.name)
        }
    }

    fun clientError(message: String): ServiceResult<*> {
        return ServiceResult.clientError<Any>(message)
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