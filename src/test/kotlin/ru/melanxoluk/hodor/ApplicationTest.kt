package ru.melanxoluk.hodor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import ru.melanxoluk.hodor.server.controllers.AboutController
import kotlin.test.Test
import kotlin.test.assertEquals


class ApplicationTest {
    private val mapper = jacksonObjectMapper()

    @Test
    fun testAbout() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "/api/v1/about")) {
            assertEquals(AboutController.about, mapper.readValue(response.content!!))
        }
    }
}