package com.melanxoluk.hodor.server

import com.melanxoluk.hodor.domain.StorageContext
import com.melanxoluk.hodor.domain.context.repositories.AppContextRepository
import com.melanxoluk.hodor.domain.context.repositories.UserContextRepository
import com.melanxoluk.hodor.domain.context.repositories.UsernameContextRepository
import com.melanxoluk.hodor.domain.context.repositories.UsersRolesContextRepository
import com.melanxoluk.hodor.domain.entities.repositories.*
import com.melanxoluk.hodor.secure.PasswordHasher
import com.melanxoluk.hodor.secure.TokenService
import com.melanxoluk.hodor.server.controllers.AboutController
import com.melanxoluk.hodor.server.controllers.AuthController
import com.melanxoluk.hodor.server.controllers.Controller
import com.melanxoluk.hodor.server.controllers.HodorUsersController
import com.melanxoluk.hodor.services.SimpleLoginService
import com.melanxoluk.hodor.services.UsersService
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.locations.Locations
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
    // todo:
    //   not allow validation of token
    //   from browser

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
        mapOf(
            "baseUrl" to "/api/v1",
            "app" to this)
    })
}


// ~~~ entry point with base initializations

object HodorApplication: KoinComponent {
    init {
        // necessary app beans
        val hodorModule = applicationContext {
            // domain, repositories
            bean { UsersRepository() }
            bean { UsernamePasswordsRepository() }
            bean { UserRolesRepository() }

            bean { AppsRepository() }
            bean { AppCreatorsRepository() }
            bean { AppClientsRepository() }
            bean { DefaultAppRolesRepository() }
            bean { AppRolesRepository() }

            // context repositories
            bean { AppContextRepository() }
            bean { UserContextRepository() }
            bean { UsernameContextRepository() }
            bean { UsersRolesContextRepository() }

            // services
            bean { SimpleLoginService() }
            bean { UsersService() }

            // controllers
            bean("allControllers") { listOf(
                HodorUsersController(it["baseUrl"], it["app"]),
                AboutController(it["baseUrl"], it["app"]),
                AuthController(it["baseUrl"], it["app"]))
            }

            // misc
            bean { TokenService(HodorConfig.key) }
            bean { PasswordHasher(HodorConfig.key.toByteArray()) }
        }

        // start di
        StandAloneContext.startKoin(listOf(hodorModule))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // init storage
        StorageContext.initialize(HodorConfig.databaseProperties)

        // start ktor, should apply app.conf params & continue
        // bootstrapping in App.main function which is higher
        val server = embeddedServer(Netty, commandLineEnvironment(args))

        // run server
        server.start()
    }
}
