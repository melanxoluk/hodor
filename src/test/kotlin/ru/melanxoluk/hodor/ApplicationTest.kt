package ru.melanxoluk.hodor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import ru.melanxoluk.hodor.common.UsernameLogin
import ru.melanxoluk.hodor.controllers.AboutController
import ru.melanxoluk.hodor.domain.hodorClient
import kotlin.test.Test
import kotlin.test.assertEquals


class ApplicationTest {
    private val mapper = jacksonObjectMapper()

    @Test
    fun aboutTest() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Get, "/api/v1/about")) {
            assertEquals(AboutController.about, mapper.readValue(response.content!!))
        }
    }


    @Test
    fun registerUserTest() = withTestApplication(Application::main) {
        auth(UsernameLogin("test", "test", hodorClient.uuid), "/api/v1/register")
    }

    @Test
    fun loginUserTest() = withTestApplication(Application::main) {
        auth(UsernameLogin("test", "test", hodorClient.uuid), "/api/v1/login")
    }

    private fun TestApplicationEngine.auth(login: UsernameLogin, method: String) {
        val call = handleRequest(HttpMethod.Post, method) {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(mapper.writeValueAsString(login))
        }

        with(call) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }


    @Test
    fun createApplicationTest() = withTestApplication(Application::main) {

    }


    @Test
    fun createClientTest() = withTestApplication(Application::main) {

    }
}