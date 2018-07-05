package com.melanxoluk.hodor.server.controllers.app

import com.melanxoluk.hodor.domain.HodorUserType
import com.melanxoluk.hodor.server.controllers.Controller
import com.melanxoluk.hodor.server.services.HodorUsersService
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.route
import org.koin.standalone.inject


// ~~~ some helpers to define request request body

interface HodorUser {
    val email: String?
    val password: String?
}

@Location("/login")
data class HodorUserLogin(override val email: String? = null,
                          override val password: String? = null): HodorUser

@Location("/register_regular")
data class HodorUserRegisterRegular(override val email: String? = null,
                                    override val password: String? = null): HodorUser

@Location("/register_admin")
data class HodorUserRegisterAdmin(override val email: String? = null,
                                  override val password: String? = null): HodorUser


// ~~~ logic starts here

class HodorUsersController(baseUrl: String,
                           app: Application): Controller(baseUrl, app) {

    private val hodorUsersService by inject<HodorUsersService>()

    override fun routes(): Routing.() -> Unit = {
        route("users") {

            // ~~~ regular login, retrieve necessary token to continue with system

            post<HodorUserLogin> { user ->
                if (notConfirmed(call, user)) return@post

                val loginedUser =
                    hodorUsersService
                        .login(user.email!!, user.password!!)

                call.respond(loginedUser)
            }


            // ~~~ regular registration, fill input and create new regular user

            post<HodorUserRegisterRegular> { user ->
                if (notConfirmed(call, user)) return@post

                hodorUsersService
                    .createUser(user.email!!, user.password!!)

                call.respond(HttpStatusCode.OK)
            }


            // ~~~ registration from super user, create an other odmen user

            post<HodorUserRegisterAdmin> { user ->
                // check token presence
                val token = call.token() ?: return@post

                // check auth state
                val caller =
                    hodorUsersService
                        .getUser(token)

                if (caller.isClientError) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }

                // check caller entity availability
                if (call.isNotOk(caller)) return@post

                if (caller.result!!.userType != HodorUserType.ADMIN) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }

                // check input fields
                if (notConfirmed(call, user)) return@post

                // now admin could be create
                hodorUsersService
                    .createAdmin(user.email!!, user.password!!)
                call.respond(HttpStatusCode.OK)
            }

        }
    }


    private suspend fun notConfirmed(call: ApplicationCall, user: HodorUser): Boolean {
        if (user.email == null || user.password == null) {
            call.respond(HttpStatusCode.BadRequest)
            return true
        }

        return false
    }
}