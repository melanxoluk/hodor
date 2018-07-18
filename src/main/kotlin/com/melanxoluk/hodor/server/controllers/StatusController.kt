package com.melanxoluk.hodor.server.controllers

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import org.slf4j.LoggerFactory


class StatusController(baseUrl: String,
                       app: Application): Controller(baseUrl, app) {

    override fun routes(): Route.() -> Unit = {
        get("status") {
            call.respond("mirosha you crazy")
        }
    }


    companion object {
        private val log = LoggerFactory.getLogger(StatusController::class.java)
    }
}