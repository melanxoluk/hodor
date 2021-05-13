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
import ru.melanxoluk.hodor.common.UserAuth
import ru.melanxoluk.hodor.common.UsernameLogin
import ru.melanxoluk.hodor.controllers.AboutController
import ru.melanxoluk.hodor.controllers.Apps
import ru.melanxoluk.hodor.controllers.GetAppsResp
import ru.melanxoluk.hodor.controllers.NewApp
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


    private fun TestApplicationEngine.auth(login: UsernameLogin, method: String): UserAuth {
        val call = handleRequest(HttpMethod.Post, method) {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(mapper.writeValueAsString(login))
        }

        return with(call) {
            assertEquals(HttpStatusCode.OK, response.status())
            mapper.readValue(response.content!!)
        }
    }

    @Test
    fun registerUserTest(): Unit = withTestApplication(Application::main) {
        val auth = auth(UsernameLogin("test", "test", hodorClient.uuid), "/api/v1/register")
        assertEquals("test", auth.me.username)
    }

    @Test
    fun loginUserTest(): Unit = withTestApplication(Application::main) {
        val auth = auth(UsernameLogin("test", "test", hodorClient.uuid), "/api/v1/login")
        assertEquals("test", auth.me.username)
    }

    @Test
    fun getMeTest(): Unit = withTestApplication(Application::main) {
        val auth = auth(UsernameLogin("test", "test", hodorClient.uuid), "/api/v1/login")
        val call = handleRequest(HttpMethod.Get, "/api/v1/me") {
            addHeader(HttpHeaders.Authorization, auth.accessToken)
        }

        with(call) {
            assertEquals(auth.me, mapper.readValue(response.content!!))
        }
    }

    @Test
    fun updateUserTest(): Unit = withTestApplication(Application::main) {
        val auth = auth(UsernameLogin("test", "test", hodorClient.uuid), "/api/v1/login")
        val me = auth.me.copy(data = "test")

        val updateUserCall = handleRequest(HttpMethod.Put, "/api/v1/users") {
            addHeader(HttpHeaders.Authorization, auth.accessToken)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(mapper.writeValueAsString(me))
        }

        val getMeCall = handleRequest(HttpMethod.Get, "/api/v1/me") {
            addHeader(HttpHeaders.Authorization, auth.accessToken)
        }

        with(getMeCall) {
            assertEquals(me, mapper.readValue(response.content!!))
        }
    }

    @Test
    fun createApplicationTest(): Unit = withTestApplication(Application::main) {
        val auth = auth(UsernameLogin("test", "test", hodorClient.uuid), "/api/v1/login")

        val newApp = NewApp("test")
        val createAppCall = handleRequest(HttpMethod.Post, "/api/v1/apps") {
            addHeader(HttpHeaders.Authorization, auth.accessToken)
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(mapper.writeValueAsString(newApp))
        }

        with(createAppCall) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun getAppsTest(): Unit = withTestApplication(Application::main) {
        val auth = auth(UsernameLogin("test", "test", hodorClient.uuid), "/api/v1/login")

        val getAppsCall = handleRequest(HttpMethod.Get, "/api/v1/apps") {
            addHeader(HttpHeaders.Authorization, auth.accessToken)
        }

        with(getAppsCall) {
            val apps = mapper.readValue<Apps>(response.content!!)
            assert(apps.apps.isNotEmpty())
        }
    }


    @Test
    fun createClientTest() = withTestApplication(Application::main) {

    }
}