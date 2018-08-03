package com.melanxoluk.hodor.server.controllers

import com.melanxoluk.hodor.server.services.HodorUsersService
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import org.koin.standalone.inject


class HodorUsersController(baseUrl: String,
                           app: Application): Controller(baseUrl, app) {

    //private val hodorUsersService by inject<HodorUsersService>()

    override fun routes(): Route.() -> Unit = {
        route("users") {

            // ~~~ regular login, retrieve necessary token to continue with system

            post {
                /*val user = call.receive(EmailPassReq::class)
                if (notConfirmed(call, user)) return@post

                val loginedUser =
                    hodorUsersService
                        .login(user.email!!, user.password!!)

                call.respond(loginedUser)*/
            }


            // ~~~ regular registration, fill input and create new regular user

            post {
                /*val user = call.receive(EmailPassReq::class)
                if (notConfirmed(call, user)) return@post

                hodorUsersService
                    .createUser(user.email!!, user.password!!)

                call.respond(HttpStatusCode.OK)*/
            }


            // ~~~ registration from super user, create an other odmen user

            post {
                /*val user = call.receive(EmailPassReq::class)

                // check token presence
                val token = token() ?: return@post

                // check auth state
                val caller =
                    hodorUsersService
                        .getUser(token)

                if (caller.isClientError) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }

                // check caller entity availability
                if (call.respond(caller)) return@post

                if (caller.result!!.userType != HodorUserType.ADMIN) {
                    call.respond(HttpStatusCode.Forbidden)
                    return@post
                }

                // check input fields
                if (notConfirmed(call, user)) return@post

                // now admin could be create
                hodorUsersService
                    .createAdmin(user.email!!, user.password!!)
                call.respond(HttpStatusCode.OK)*/
            }

        }
    }


    private suspend fun notConfirmed(call: ApplicationCall, user: EmailPassReq): Boolean {
        if (user.email == null || user.password == null) {
            call.respond(HttpStatusCode.BadRequest)
            return true
        }

        return false
    }
}