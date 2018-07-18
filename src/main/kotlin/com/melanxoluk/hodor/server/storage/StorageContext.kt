package com.melanxoluk.hodor.server.storage

import com.melanxoluk.hodor.server.DatabaseProperties
import com.melanxoluk.hodor.server.storage.repositories.HodorUsersRepository
import com.melanxoluk.hodor.server.storage.repositories.ApplicationUsersRepository
import com.melanxoluk.hodor.server.storage.repositories.EmailPasswordsAuthRepository
import com.melanxoluk.hodor.server.storage.repositories.EmailPasswordsRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.Driver
import kotlin.reflect.jvm.jvmName


// todo: don't like such initialization flow, but why and how an other... don't know
object StorageContext {
    private const val postgresJdbcTemplate = "jdbc:postgresql:%s"

    fun initialize(databaseProperties: DatabaseProperties) {
        val url = String.format(postgresJdbcTemplate, databaseProperties.name)
        Database.connect(url,
                         Driver::class.jvmName,
                         databaseProperties.user,
                         databaseProperties.password)

        // simplest way of keeping actual state of tables
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                EmailPasswordsRepository.EmailPasswordTable,
                EmailPasswordsAuthRepository.EmailPasswordsAuthenticationTable,
                HodorUsersRepository.HodorUserTable,
                ApplicationUsersRepository.UsersTable)
        }
    }
}