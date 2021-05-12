package ru.melanxoluk.hodor.controllers

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.util.pipeline.PipelineContext
import org.koin.core.component.get
import ru.melanxoluk.hodor.common.UsernameLogin
import ru.melanxoluk.hodor.secure.TokenService
import ru.melanxoluk.hodor.services.ServiceResult
import ru.melanxoluk.hodor.services.LoginService
import ru.melanxoluk.hodor.common.Token
import ru.melanxoluk.hodor.services.UsersService


@Suppress("SENSELESS_COMPARISON")
class AuthController(baseUrl: String, app: Application): Controller(baseUrl, app) {
    private val loginService = get<LoginService>()
    private val usersService = get<UsersService>()
    private val tokenService = get<TokenService>()

    override fun routes(): Route.() -> Unit = {
        get("is_valid") {
            val token = token()
            if (tokenService.isValidExpiration(token)) {
                ok()
            } else {
                unauthorized()
            }
        }

        // todo: provide description how and why need token refreshing
        get("refresh") {
            val token = token()
            if (tokenService.isValidExpiration(token)) {
                val refreshed = tokenService.refresh(token)
                val tokenRes = Token(refreshed)
                this.respond(ServiceResult.ok(tokenRes))
            } else {
                unauthorized()
            }
        }


        suspend fun PipelineContext<*, ApplicationCall>.assertLogin(login: UsernameLogin) = login.also {
            assert(
                login.username to "'username' is not provided",
                login.password to "'password' is not provided",
                login.client to "'client' is not provided"
            )
        }

        post("login") {
            respond(loginService.login(assertLogin(parse())))
        }

        post("register") {
            respond(usersService.create(assertLogin(parse())))
        }
    }
}
