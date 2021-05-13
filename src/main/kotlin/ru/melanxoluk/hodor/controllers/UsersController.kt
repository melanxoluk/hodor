package ru.melanxoluk.hodor.controllers

import io.ktor.application.Application
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import org.koin.core.component.get
import ru.melanxoluk.hodor.services.UsersService


class UsersController(baseUrl: String, app: Application): Controller(baseUrl, app) {
    private val usersService = get<UsersService>()

    override fun routes(): Route.() -> Unit = {
        get("/me") {
            respond(usersService.getMe(token()))
        }

        put("/users") {
            respond(usersService.update(parse()))
        }
    }
}