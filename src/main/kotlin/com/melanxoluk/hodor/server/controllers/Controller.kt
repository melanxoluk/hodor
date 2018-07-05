package com.melanxoluk.hodor.server.controllers

import com.melanxoluk.hodor.server.services.ServiceResult
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.route
import io.ktor.routing.routing
import org.koin.standalone.KoinComponent


abstract class Controller(val baseUrl: String = "",
                          val app: Application): HasRouting, KoinComponent {
    init {
        app.routing {
            route(baseUrl) {
                routes()(this@routing)
            }
        }
    }


    suspend fun ApplicationCall.token(): String? {
        val token = parameters[AUTH]
        if (token == null) {
            respond(HttpStatusCode.Unauthorized)
        }

        return token
    }

    suspend fun <T> ApplicationCall.isNotOk(serviceResult: ServiceResult<T>): Boolean {
        if (serviceResult.isServerError) {
            respond(HttpStatusCode.InternalServerError)
            return true
        } else if (serviceResult.isClientError) {
            respond(HttpStatusCode.BadRequest)
            return true
        }

        return true
    }


    companion object {
        private val AUTH = "Authorize"
    }
}

interface HasRouting {
    fun routes(): Routing.() -> Unit = {}
}