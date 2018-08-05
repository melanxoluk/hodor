package com.melanxoluk.hodor.server.storage

import com.melanxoluk.hodor.allNotNull
import com.melanxoluk.hodor.domain.hodorApp
import com.melanxoluk.hodor.domain.hodorSuperUser
import com.melanxoluk.hodor.domain.hodorSuperUsernamePassword
import com.melanxoluk.hodor.domain.hodorWebClient
import com.melanxoluk.hodor.server.DatabaseProperties
import com.melanxoluk.hodor.server.storage.repositories.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.postgresql.Driver
import kotlin.reflect.jvm.jvmName


// todo: don't like such initialization flow, but don't know how to make better
object StorageContext: KoinComponent {
    private const val postgresJdbcTemplate = "jdbc:postgresql:%s"

    private val usernamePasswordsRepository = get<UsernamePasswordsRepository>()
    private val applicationsRepository = get<ApplicationsRepository>()
    private val appClientsRepository = get<AppClientsRepository>()
    private val usersRepository = get<UsersRepository>()


    fun initialize(databaseProperties: DatabaseProperties) {
        val url = String.format(postgresJdbcTemplate, databaseProperties.name)
        val connection = Database.connect(
            url,
            Driver::class.jvmName,
            databaseProperties.user,
            databaseProperties.password)

        // todo: determine points for maintain database from application & external from migrations
        // simplest way of keeping actual state of tables
        transaction {
            TransactionManager.current().exec(
                "CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "application BIGINT NOT NULL, " +
                    "properties TEXT NOT NULL, " +
                    "uuid uuid NOT NULL)")

            SchemaUtils.create(ApplicationsRepository.ApplicationsTable)

            SchemaUtils.createMissingTablesAndColumns(
                UsernamePasswordsRepository.UsernamePasswordTable,
                //ApplicationsRepository.ApplicationsTable,
                AppClientsRepository.AppClientsTable,
                UsersRepository.UserTable)
        }

        // check status of hodor app entities
        val foundUser = usersRepository.findByUuid(hodorSuperUser.uuid)
        val foundApp = applicationsRepository.findByUuid(hodorApp.uuid)
        val found = allNotNull(foundUser, foundApp)
        if (found) return

        // but if not found necessary to initialize hodor app context
        // need to find reference constraint of user field to app, then:
        // 1) alter remove constraint
        // 2) insert hodor user
        // 3) insert hodor app
        // 4) alter add constraint back
        transaction {
            TransactionManager.current().exec("ALTER TABLE users DROP CONSTRAINT users_application_fkey")

            val user = usersRepository.create(hodorSuperUser)
            val userPass = usernamePasswordsRepository.create(hodorSuperUsernamePassword)
            val app = applicationsRepository.create(hodorApp)
            val client = appClientsRepository.create(hodorWebClient)

            TransactionManager.current().exec("ALTER TABLE users ADD FOREIGN KEY (application) REFERENCES applications(id) ON DELETE RESTRICT")
        }
    }
}