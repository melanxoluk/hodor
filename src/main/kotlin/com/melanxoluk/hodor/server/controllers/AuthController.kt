package com.melanxoluk.hodor.server.controllers

import com.melanxoluk.hodor.common.UsernameLogin
import com.melanxoluk.hodor.secure.TokenService
import com.melanxoluk.hodor.services.ServiceResult
import com.melanxoluk.hodor.services.SimpleLoginService
import com.melanxoluk.hodor.services.Token
import io.ktor.application.Application
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.koin.core.component.get


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

            if (tokenService.isValidExpiration(token)) {
                ok()
            } else {
                unauthorized()
            }
        }

        get("refresh") {
            val token = token()
            if (token == null) {
                badRequest()
                return@get
            }

            if (tokenService.isValidExpiration(token)) {
                val refreshed = tokenService.refresh(token)
                val tokenRes = Token(refreshed)
                this.respond(ServiceResult.ok(tokenRes))
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
            this.respond(newToken)
        }
    }
}
