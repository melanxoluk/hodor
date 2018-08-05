package com.melanxoluk.hodor.server

import com.melanxoluk.hodor.secure.PasswordHasher
import com.melanxoluk.hodor.secure.TokenGenerator
import com.melanxoluk.hodor.server.controllers.Controller
import com.melanxoluk.hodor.server.controllers.AuthController
import com.melanxoluk.hodor.server.controllers.StatusController
import com.melanxoluk.hodor.server.controllers.HodorUsersController
import com.melanxoluk.hodor.server.services.AuthService
import com.melanxoluk.hodor.server.services.HodorUsersService
import com.melanxoluk.hodor.server.storage.StorageContext
import com.melanxoluk.hodor.server.storage.repositories.*
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.locations.Locations
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.dsl.module.applicationContext
import org.koin.ktor.ext.get
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext


// ~~~ ktor server initialization

fun Application.main() {
    // setup necessary features
    install(CORS) {
        allowCredentials = true
        allowSameOrigin = true
        methods += HttpMethod.DefaultMethods
        anyHost()
    }

    install(Locations)
    install(CallLogging)
    install(DefaultHeaders)
    install(ContentNegotiation) {
        gson {
            // registerTypeAdapter(DateTime::class.java, JodaDateTimeAdapter())
            setPrettyPrinting()
            setLenient()
        }
    }

    routing {
        trace { application.log.info(it.buildText()) }
    }


    // initialize routing controllers
    get<List<Controller>>(parameters = {
        mapOf("baseUrl" to "/v1", "app" to this)
    })

    routing {
        route("test") {
            get("check") {
                call.respond("fuck")
            }
        }
    }
}


// ~~~ entry point with base initializations

object HodorApplication: KoinComponent {
    init {
        // necessary app beans
        val hodorModule = applicationContext {
            // domain, repositories
            bean { UsernamePasswordsRepository() }
            bean { EmailPasswordsAuthRepository() }
            bean { AppUsersRepository() }
            bean { EmailPasswordsRepository() }
            bean { ApplicationsRepository() }
            bean { HodorUsersRepository() }
            bean { AppClientsRepository() }
            bean { UsersRepository() }
            bean { AuthRepository() }

            // services
            bean { HodorUsersService() }
            bean { AuthService() }

            // controllers
            bean("allControllers") { listOf(
                HodorUsersController(it["baseUrl"], it["app"]),
                StatusController(it["baseUrl"], it["app"]),
                AuthController(it["baseUrl"], it["app"]))
            }

            // misc
            bean { TokenGenerator(HodorConfig.key) }
            bean { PasswordHasher(HodorConfig.key.toByteArray()) }
        }

        // start di
        StandAloneContext.startKoin(listOf(hodorModule))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // init storage
        StorageContext.initialize(HodorConfig.databaseProperties)

        // start ktor, should apply application.conf params & continue
        // bootstrapping in Application.main function which is higher
        val server = embeddedServer(Netty, commandLineEnvironment(args))

        // run server
        server.start()
    }
}



