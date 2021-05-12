package ru.melanxoluk.hodor

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract


object HodorConfig {
    val key: String
    val superUser: ru.melanxoluk.hodor.SuperUser
    val databaseProperties: ru.melanxoluk.hodor.DatabaseProperties

    init {
        val config = ConfigFactory.load()

        ru.melanxoluk.hodor.HodorConfig.key = config.extract("key")
        ru.melanxoluk.hodor.HodorConfig.superUser = config.extract("super-user")
        ru.melanxoluk.hodor.HodorConfig.databaseProperties = config.extract("database")
    }
}


data class SuperUser(var login: String = "",
                     var password: String = "")

data class DatabaseProperties(var name: String = "",
                              var user: String = "",
                              var password: String = "")