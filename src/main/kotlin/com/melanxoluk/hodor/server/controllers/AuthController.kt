package com.melanxoluk.hodor.server.controllers

import com.melanxoluk.hodor.server.services.AuthService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.koin.standalone.get


class EmailPassReq {
    var email: String? = null
    var password: String? = null
}

class AuthController(baseUrl: String,
                     app: Application): Controller(baseUrl, app) {

    private val authService = get<AuthService>()


    override fun routes(): Route.() -> Unit = {
        get("is_valid") {
            token()
            ok()
        }

        // todo: provide nullable option of client token
        // todo: to determine where from request came
        post("simple_login") {
            val emailPassReq = call.receive<EmailPassReq>()

            if (!assert(emailPassReq.email, emailPassReq.password)) return@post

            respond(authService.simpleLogin(
                emailPassReq.email!!,
                emailPassReq.password!!))
        }
    }
}