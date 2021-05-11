package com.melanxoluk.hodor

import com.melanxoluk.hodor.domain.StorageContext
import com.melanxoluk.hodor.domain.context.repositories.AppContextRepository
import com.melanxoluk.hodor.domain.context.repositories.UserContextRepository
import com.melanxoluk.hodor.domain.context.repositories.UsernameContextRepository
import com.melanxoluk.hodor.domain.context.repositories.UsersRolesContextRepository
import com.melanxoluk.hodor.domain.entities.repositories.*
import com.melanxoluk.hodor.secure.PasswordHasher
import com.melanxoluk.hodor.secure.TokenService
import com.melanxoluk.hodor.server.controllers.*
import com.melanxoluk.hodor.services.AppsService
import com.melanxoluk.hodor.services.ClientsService
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
import io.ktor.http.parametersOf
import io.ktor.locations.Locations
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.netty.handler.ssl.ApplicationProtocolConfig
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.get


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

    install(Koin) {
        modules(module {
            single { UsersRepository() }
            single { UsernamePasswordsRepository() }
            single { UserRolesRepository() }

            single { AppsRepository() }
            single { AppCreatorsRepository() }
            single { AppClientsRepository() }
            single { DefaultAppRolesRepository() }
            single { AppRolesRepository() }

            // context repositories
            single { AppContextRepository() }
            single { UserContextRepository() }
            single { UsernameContextRepository() }
            single { UsersRolesContextRepository() }

            // services
            single { SimpleLoginService() }
            single { ClientsService() }
            single { UsersService() }
            single { AppsService() }

            // controllers
            single{ listOf(
                AboutController(it[0], it[1]),
                AuthController(it[0], it[1]),
                AppsController(it[0], it[1]),
                ClientsController(it[0], it[1]))
            }

            // misc
            single { TokenService(HodorConfig.key) }
            single { PasswordHasher(HodorConfig.key.toByteArray()) }
        })
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

    // init storage
    StorageContext.initialize(HodorConfig.databaseProperties)

    // initialize routing controllers
    get<List<Controller>>(parameters = {
        parametersOf("/api/v1", this)
    })
}


// ~~~ entry point with base initializations

object HodorApplication: KoinComponent {
    @JvmStatic
    fun main(args: Array<String>) {
        // start ktor, should apply app.conf params & continue
        // bootstrapping in App.main function which is higher
        val server = embeddedServer(Netty, commandLineEnvironment(args))

        // run server
        server.start()
    }
}
