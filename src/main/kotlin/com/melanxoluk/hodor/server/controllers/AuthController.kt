package com.melanxoluk.hodor.server.controllers

import com.melanxoluk.hodor.common.UsernameLogin
import com.melanxoluk.hodor.secure.TokenService
import com.melanxoluk.hodor.services.SimpleLoginService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.koin.standalone.get


@Suppress("SENSELESS_COMPARISON")
class AuthController(baseUrl: String,
                     app: Application): Controller(baseUrl, app) {

    private val loginService = get<SimpleLoginService>()
    private val tokenService = get<TokenService>()

    override fun routes(): Route.() -> Unit = {
        get("is_valid") {
            val token = token()
            if (token == null) {
                badRequest()
                return@get
            }

            if (tokenService.validate(token)) {
                ok()
            } else {
                unauthorized()
            }
        }

        post("simple_login") {
            val login = parseOrNull<UsernameLogin>() ?: return@post
            if (!assert(login.username to "username is not provided",
                        login.password to "password is not provided",
                        login.client to "client is not provided")) {
                return@post
            }

            val newToken = loginService.simpleLogin(login)
            respond(newToken)
        }
    }
}
