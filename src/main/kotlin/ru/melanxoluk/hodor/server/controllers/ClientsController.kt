package ru.melanxoluk.hodor.server.controllers

import ru.melanxoluk.hodor.services.ClientsService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.koin.core.component.get
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
            this.respond(clientsService.getAll(appUuid))
        }

        post("apps/{uuid}/clients") {
            val userContext = validateHodor() ?: return@post
            val newClient = parse<NewClient>() ?: return@post
            assert(newClient.type to "type is not provided")

            val sAppUuid = call.parameters["uuid"]
            val appUuid = UUID.fromString(sAppUuid)

            this.respond(clientsService.create(appUuid, newClient.type))
        }
    }
}
