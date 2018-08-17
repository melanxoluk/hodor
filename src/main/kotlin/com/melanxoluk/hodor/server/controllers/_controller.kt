package com.melanxoluk.hodor.server.controllers

import com.melanxoluk.hodor.common.Result
import com.melanxoluk.hodor.common.negative
import com.melanxoluk.hodor.common.positive
import com.melanxoluk.hodor.services.ServiceResult
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.pipeline.PipelineContext
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.routing
import org.koin.standalone.KoinComponent
import org.slf4j.LoggerFactory


abstract class Controller(val baseUrl: String = "",
                          val app: Application): HasRouting, KoinComponent {
    init {
        app.routing {
            route(baseUrl) {
                log.info("Start setting route to base url: $baseUrl")
                routes()(this@route)
            }
        }
    }


    suspend fun PipelineContext<*, ApplicationCall>.token(): String? {
        val token = call.parameters[AUTH]
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


    suspend fun <T> PipelineContext<*, ApplicationCall>.
        respond(serviceResult: ServiceResult<T>) {

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

    companion object {
        private val AUTH = "Authorize"
        private val log = LoggerFactory.getLogger(Controller::class.java)
    }
}

interface HasRouting {
    fun routes(): Route.() -> Unit = {}
}