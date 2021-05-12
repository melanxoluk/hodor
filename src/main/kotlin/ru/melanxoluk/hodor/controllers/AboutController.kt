package ru.melanxoluk.hodor.controllers

import ru.melanxoluk.hodor.domain.hodorClient
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get


data class About(
    var version: String = "",
    var name: String = "",
    var client: String = "",
    var systemName: String? = null,
    var shortName: String? = null,
    var fullName: String? = null,
    var logo: String? = null
)

class AboutController(path: String, app: Application): Controller(path, app) {
    companion object {
        val about = About("0.1", "Hodor", hodorClient.uuid.toString())
    }


    override fun routes(): Route.() -> Unit = {
        get("about") {
            call.respond(about)
        }
    }
}


