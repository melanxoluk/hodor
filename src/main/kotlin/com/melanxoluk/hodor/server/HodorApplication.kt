package com.melanxoluk.hodor.server

import com.melanxoluk.hodor.secure.TokenGenerator
import com.melanxoluk.hodor.server.storage.StorageContext
import com.melanxoluk.hodor.server.storage.repositories.HodorUsersRepository
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.dsl.module.applicationContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject


// ~~~ ktor server initialization

fun Application.main() {
    // setup necessary features
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            // registerTypeAdapter(DateTime::class.java, JodaDateTimeAdapter())
            setPrettyPrinting()
            setLenient()
        }
    }

    // initialize routing controllers
    //val factories: List<(Application) -> Controller> by inject()
    //factories.forEach { it(this) }
}


// ~~~ entry point with base initializations

object HodorApplication: KoinComponent {
    init {
        // necessary app beans
        val hodorModule = applicationContext {
            bean { TokenGenerator() }
        }

        // start di
        StandAloneContext.startKoin(listOf(hodorModule))
    }


    private val hodorUsersRepository by inject<HodorUsersRepository>()

    @JvmStatic
    fun main(args: Array<String>) {
        // init storage
        StorageContext.initialize(HodorConfig.databaseProperties)
        StorageContext.createTables()

        // start ktor, should apply application.conf params & continue
        // bootstrapping in Application.main function which is higher
        val server = embeddedServer(Netty, commandLineEnvironment(args))

        // run server
        server.start()
    }
}



