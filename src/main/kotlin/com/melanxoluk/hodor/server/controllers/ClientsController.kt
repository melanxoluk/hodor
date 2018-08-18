package com.melanxoluk.hodor.server.controllers

import com.melanxoluk.hodor.services.ClientsService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.koin.standalone.get
import java.util.*


private class NewClient(val type: String)


class ClientsController(baseUrl: String,
                        app: Application) : Controller(baseUrl, app) {

    private val clientsService = get<ClientsService>()


    override fun routes(): Route.() -> Unit = {
        get("apps/{uuid}/clients") {
            val userContext = validateHodor() ?: return@get
            val sAppUuid = call.parameters["uuid"]
            val appUuid = UUID.fromString(sAppUuid)
            respond(clientsService.getAll(appUuid))
        }

        post("apps/{uuid}/clients") {
            val userContext = validateHodor() ?: return@post
            val newClient = parseOrNull<NewClient>() ?: return@post
            if (!assert(newClient.type to "type is not provided")) {
                return@post
            }
            val sAppUuid = call.parameters["uuid"]
            val appUuid = UUID.fromString(sAppUuid)

            respond(clientsService.create(appUuid, newClient.type))
        }
    }
}
