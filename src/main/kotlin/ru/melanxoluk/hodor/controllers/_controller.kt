package ru.melanxoluk.hodor.controllers

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.slf4j.LoggerFactory
import ru.melanxoluk.hodor.domain.context.UserContext
import ru.melanxoluk.hodor.domain.hodorApp
import ru.melanxoluk.hodor.secure.TokenService
import ru.melanxoluk.hodor.services.ServiceResult
import ru.melanxoluk.hodor.services.UsersService


abstract class Controller(baseUrl: String = "", app: Application): HasRouting, KoinComponent {
    companion object {
        private const val AUTH = "Authorization"
        private val log = LoggerFactory.getLogger(Controller::class.java)
    }

    private val tokenService = get<TokenService>()
    private val usersService = get<UsersService>()

    init {
        app.routing {
            route(baseUrl) {
                log.info("Start setting route to base url: $baseUrl")
                routes()(this@route)
            }
        }
    }


    suspend fun PipelineContext<*, ApplicationCall>.getHodorUser(): Result<UserContext> {
        return getUser().map { user ->
            assert(isHodor(user) to "Not Hodor user")
            user
        }
    }

    suspend fun PipelineContext<*, ApplicationCall>.getUser(): Result<UserContext> {
        return usersService.get(token())
    }

    fun isHodor(userContext: UserContext): Boolean {
        return userContext.appId == hodorApp.id
    }


    suspend fun PipelineContext<*, ApplicationCall>.token(): String {
        val token = call.request.headers[AUTH]
        if (token == null) {
            call.respond(HttpStatusCode.Unauthorized)
            throw IllegalStateException("Auth header not found")
        }

        if (!tokenService.isValidExpiration(token))
            unauthorized()

        return token
    }

    // todo: provide additional message to client
    suspend fun PipelineContext<*, ApplicationCall>.assert(vararg args: Any?): Boolean {
        if(args.any { it == null }) {
            call.respond(HttpStatusCode.BadRequest)
            return false
        }

        return true
    }

    suspend fun PipelineContext<*, ApplicationCall>.assert(vararg args: Pair<Any?, String>) {
        val iter = args.iterator()
        while (iter.hasNext()) {
            val pair = iter.next()
            if (pair.first == null || pair.first == false) {
                call.respond(HttpStatusCode.BadRequest, pair.second)
                throw IllegalStateException("Assertion failed: ${pair.second}")
            }
        }
    }

    suspend fun PipelineContext<*, ApplicationCall>.ok() {
        call.respond(HttpStatusCode.OK)
    }

    suspend fun PipelineContext<*, ApplicationCall>.badRequest() {
        call.respond(HttpStatusCode.BadRequest)
    }

    suspend fun PipelineContext<*, ApplicationCall>.unauthorized() {
        call.respond(HttpStatusCode.Unauthorized)
        throw IllegalStateException("Unauthorized")
    }


    suspend fun <T> PipelineContext<*, ApplicationCall>.respond(serviceResult: ServiceResult<T>) {
        when {
            serviceResult.isOk -> {
                call.respond(serviceResult)
            }

            serviceResult.isServerError -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    serviceResult.errorMessage ?: "")
            }

            serviceResult.isClientError -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    serviceResult.errorMessage ?: "")
            }

            // something wrong with flow, not accepted behavior
            else -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    serviceResult.errorMessage ?: "")
            }
        }
    }

    suspend inline fun <reified T> PipelineContext<*, ApplicationCall>.respond(result: Result<T>) {
        when {
            result.isSuccess -> {
                call.respond(result.getOrThrow()!!)
            }

            result.isFailure -> {
                val exception = result.exceptionOrNull()
                call.respond(
                    HttpStatusCode.BadRequest,
                    exception?.message ?: "")
            }

            // something wrong with flow, not accepted behavior
            else -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Internal exception")
            }
        }
    }


    suspend inline fun <reified T : Any> PipelineContext<*, ApplicationCall>.parse(): T {
        val body = call.receiveOrNull<T>()
        if (body != null)
            return body

        call.respond(HttpStatusCode.BadRequest)
        throw IllegalStateException("body is null")
    }
}

interface HasRouting {
    fun routes(): Route.() -> Unit = {}
}