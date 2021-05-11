package com.melanxoluk.hodor.server.controllers

import com.melanxoluk.hodor.common.Result
import com.melanxoluk.hodor.common.negative
import com.melanxoluk.hodor.common.positive
import com.melanxoluk.hodor.domain.context.UserContext
import com.melanxoluk.hodor.domain.context.repositories.UserContextRepository
import com.melanxoluk.hodor.domain.hodorApp
import com.melanxoluk.hodor.secure.TokenService
import com.melanxoluk.hodor.services.ServiceResult
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


abstract class Controller(val baseUrl: String = "",
                          val app: Application): HasRouting, KoinComponent {
    companion object {
        private const val AUTH = "Authorization"
        private val log = LoggerFactory.getLogger(Controller::class.java)
    }


    private val userContextRepository = get<UserContextRepository>()
    private val tokenService = get<TokenService>()

    init {
        app.routing {
            route(baseUrl) {
                log.info("Start setting route to base url: $baseUrl")
                routes()(this@route)
            }
        }
    }


    suspend fun PipelineContext<*, ApplicationCall>.validateHodor(): UserContext? {
        val userContext = validateToken()
        if (userContext.isError) {
            return null
        }

        if (!isHodor(userContext.result!!)) {
            return null
        }

        return userContext.result
    }

    suspend fun PipelineContext<*, ApplicationCall>.validateToken(): ServiceResult<UserContext> {
        val token = call.request.headers[AUTH]
        if (token == null) {
            call.respond(HttpStatusCode.Unauthorized)
            return ServiceResult.clientError("Auth header not provided")
        }

        val isValid = tokenService.isValidExpiration(token)
        if (!isValid) {
            call.respond(HttpStatusCode.Unauthorized)
            return ServiceResult.clientError("Expired auth provided")
        }

        return ServiceResult.ok(userContextRepository.get(token))
    }

    suspend fun PipelineContext<*, ApplicationCall>.isHodor(userContext: UserContext): Boolean {
        if (userContext.app != hodorApp) {
            call.respond(HttpStatusCode.BadRequest)
            return false
        }

        return true
    }


    suspend fun PipelineContext<*, ApplicationCall>.token(): String? {
        val token = call.request.headers[AUTH]
        if (token == null) {
            call.respond(HttpStatusCode.Unauthorized)
        }

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

    suspend fun PipelineContext<*, ApplicationCall>.assert(vararg args: Pair<Any?, String>): Boolean {
        val iter = args.iterator()
        while (iter.hasNext()) {
            val pair = iter.next()
            if (pair.first == null) {
                call.respond(HttpStatusCode.BadRequest, pair.second)
                return false
            }
        }

        return true
    }

    suspend fun PipelineContext<*, ApplicationCall>.ok() {
        call.respond(HttpStatusCode.OK)
    }

    suspend fun PipelineContext<*, ApplicationCall>.badRequest() {
        call.respond(HttpStatusCode.BadRequest)
    }

    suspend fun PipelineContext<*, ApplicationCall>.unauthorized() {
        call.respond(HttpStatusCode.Unauthorized)
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


    suspend inline fun <reified T : Any> PipelineContext<*, ApplicationCall>.parseOrNull(): T? {
        val body = call.receiveOrNull<T>()
        if (body == null) {
            call.respond(HttpStatusCode.BadRequest)
        }
        return body
    }

    suspend inline fun <reified T : Any> PipelineContext<*, ApplicationCall>.parse(errorMessage: String): Result<T> {
        val entity = call.receiveOrNull<T>()
            ?: return negative(errorMessage)

        return positive(entity)
    }
}

interface HasRouting {
    fun routes(): Route.() -> Unit = {}
}