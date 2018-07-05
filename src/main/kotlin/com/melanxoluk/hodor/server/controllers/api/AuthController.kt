package com.melanxoluk.hodor.server.controllers.api

import com.melanxoluk.hodor.server.controllers.Controller
import io.ktor.application.Application
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.routing.Routing


@Location("login")
data class LoginUser(val email: String, val password: String)

class AuthController(baseUrl: String,
                     app: Application): Controller(baseUrl, app) {

    override fun routes(): Routing.() -> Unit = {
        post<LoginUser> { user ->

        }
    }
}