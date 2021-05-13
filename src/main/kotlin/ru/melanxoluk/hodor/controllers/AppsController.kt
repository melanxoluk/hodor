package ru.melanxoluk.hodor.controllers

import io.ktor.application.Application
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.koin.core.component.get
import ru.melanxoluk.hodor.domain.context.AppContext
import ru.melanxoluk.hodor.domain.entities.App
import ru.melanxoluk.hodor.domain.entities.AppClient
import ru.melanxoluk.hodor.domain.entities.AppRole
import ru.melanxoluk.hodor.services.AppsService


data class NewApp(val name: String)

data class Apps(val apps: List<GetAppsResp>)

data class GetAppsResp(
    val app: App,
    val roles: List<AppRole>,
    val defaultRoles: List<AppRole>,
    val clients: List<AppClient>) {
    companion object {
        fun create(apps: List<AppContext>) = Apps(apps.map {
            GetAppsResp(it.app, it.roles, it.defaultRoles, it.clients)
        })
    }

    constructor(context: AppContext) :
        this(context.app,
             context.roles,
             context.defaultRoles,
             context.clients)
}



class AppsController(baseUrl: String, app: Application): Controller(baseUrl, app) {
    private val appsService = get<AppsService>()

    override fun routes(): Route.() -> Unit = {
        get("apps") {
            respond(getHodorUser().map { user ->
                GetAppsResp.create(appsService.getAll(user))
            })
        }

        post("apps") {
            val newApp = parse<NewApp>()
            assert(newApp.name to "name is not provided")

            respond(getHodorUser().map { user ->
                GetAppsResp(appsService.create(user, newApp.name))
            })
        }
    }
}
