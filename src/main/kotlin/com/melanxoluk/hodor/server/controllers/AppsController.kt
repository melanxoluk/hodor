package com.melanxoluk.hodor.server.controllers

import com.melanxoluk.hodor.domain.context.AppContext
import com.melanxoluk.hodor.domain.entities.App
import com.melanxoluk.hodor.domain.entities.AppRole
import com.melanxoluk.hodor.services.AppsService
import com.melanxoluk.hodor.services.ServiceResult
import io.ktor.application.Application
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.koin.core.component.get


private class NewApp(val name: String)

private class Apps(val apps: List<AppResp>)

private class AppResp(val app: App,
              val roles: List<AppRole>,
              val defaultRoles: List<AppRole>) {
    companion object {
        fun create(apps: List<AppContext>) = Apps(apps.map {
            AppResp(it.app, it.roles, it.defaultRoles)
        })
    }

    constructor(context: AppContext) :
        this(context.app,
             context.roles,
             context.defaultRoles)
}



class AppsController(baseUrl: String,
                     app: Application): Controller(baseUrl, app) {

    private val appsService = get<AppsService>()

    override fun routes(): Route.() -> Unit = {
        get("apps") {
            val userContext = validateHodor() ?: return@get
            val apps = appsService.getAll(userContext)
            this.respond(apps.map { appContexts ->
                AppResp.create(appContexts)
            })
        }

        post("apps") {
            val userContext = validateHodor() ?: return@post
            val newApp = parseOrNull<NewApp>() ?: return@post
            if (!assert(newApp.name to "name is not provided")) {
                return@post
            }

            this.respond(appsService
                .create(userContext, newApp.name)
                .map { context -> AppResp(context) })
        }
    }
}
