package com.melanxoluk.hodor.server.storage

import com.melanxoluk.hodor.domain.hodorApp
import com.melanxoluk.hodor.server.DatabaseProperties
import com.melanxoluk.hodor.server.storage.repositories.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.postgresql.Driver
import kotlin.reflect.jvm.jvmName


// todo: don't like such initialization flow, but why and how an other... don't know
object StorageContext: KoinComponent {
    private const val postgresJdbcTemplate = "jdbc:postgresql:%s"

    private val emailPasswordsRepository = get<EmailPasswordsRepository>()
    private val applicationsRepository = get<ApplicationsRepository>()
    private val usersRepository = get<UsersRepository>()


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

        // check status of hodor app entities
        val foundApp = applicationsRepository.findByUuid(hodorApp.uuid)

    }
}