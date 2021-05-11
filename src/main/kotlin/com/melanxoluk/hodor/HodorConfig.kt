package com.melanxoluk.hodor

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract


object HodorConfig {
    val key: String
    val superUser: SuperUser
    val databaseProperties: DatabaseProperties

    init {
        val config = ConfigFactory.load()

        key = config.extract("key")
        superUser = config.extract("super-user")
        databaseProperties = config.extract("database")
    }
}


data class SuperUser(var login: String = "",
                     var password: String = "")

data class DatabaseProperties(var name: String = "",
                              var user: String = "",
                              var password: String = "")