package com.melanxoluk.hodor.domain

import com.melanxoluk.hodor.domain.entities.*
import com.melanxoluk.hodor.server.DatabaseProperties
import com.melanxoluk.hodor.server.HodorConfig
import com.melanxoluk.hodor.domain.repositories.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.postgresql.Driver
import java.util.*
import kotlin.reflect.jvm.jvmName


// todo: don't like such initialization flow, but don't know how to make better
object StorageContext: KoinComponent {
    private const val postgresJdbcTemplate = "jdbc:postgresql:%s"

    private val usernamePasswordsRepository = get<UsernamePasswordsRepository>()
    private val applicationsRepository = get<AppsRepository>()
    private val appClientsRepository = get<AppClientsRepository>()
    private val usersRolesRepository = get<UsersRolesRepository>()
    private val appRolesRepository = get<AppRolesRepository>()
    private val usersRepository = get<UsersRepository>()


    fun initialize(databaseProperties: DatabaseProperties) {
        val url = String.format(postgresJdbcTemplate, databaseProperties.name)
        Database.connect(
            url,
            Driver::class.jvmName,
            databaseProperties.user,
            databaseProperties.password)

        // refresh hodor app entities
        // fixme: alter table only when need to insert entities
        transaction {
            TransactionManager.current().exec("ALTER TABLE users DROP CONSTRAINT users_app_id_fkey")
        }

        transaction {
            initHodorUser()
            initHodorUsernamePass(hodorSuperUser)
            initHodorApp(hodorSuperUser)
            initHodorAppClient(hodorApp)
            initHodorAppRoles(hodorSuperUser, hodorApp)
        }

        transaction {
            hodorSuperUser = hodorSuperUser.copy(appId = hodorApp.id)
            usersRepository.update(hodorSuperUser)
            TransactionManager.current().exec("ALTER TABLE users ADD FOREIGN KEY (app_id) REFERENCES apps(id) ON DELETE RESTRICT")
        }
    }


    // ~~~ init app entities

    private fun initHodorUser() {
        val hodorUser =
            usersRepository
                .findWithHodorPrefix()

        hodorSuperUser = hodorUser
            ?: usersRepository.create(hodorSuperUser)
    }

    private fun initHodorUsernamePass(user: User) {
        val usernamePassword =
            usernamePasswordsRepository
                .findByUser(user)

        hodorSuperUsernamePassword = usernamePassword
            ?: usernamePasswordsRepository.create(
            hodorSuperUsernamePassword.copy(userId = user.id))
    }

    private fun initHodorApp(creator: User) {
        val app =
            applicationsRepository
                .findByCreator(creator)

        hodorApp = app
            ?: applicationsRepository.create(
            hodorApp.copy(creatorId = creator.id))
    }

    private fun initHodorAppClient(app: App) {
        val client =
            appClientsRepository
                .findByApp(app)

        hodorClient = client
            ?: appClientsRepository.create(
            hodorClient.copy(appId = app.id))
    }

    private fun initHodorAppRoles(user: User, app: App) {
        val admin =
            appRolesRepository
                .findByAppAndName(app, hodorAdminRole.name)

        hodorAdminRole = admin
            ?: appRolesRepository.create(
            hodorAdminRole.copy(appId = app.id))

        var adminUserRole = usersRolesRepository
            .findByUserAndRole(user, hodorAdminRole)

        adminUserRole = adminUserRole
            ?: usersRolesRepository.create(
            UsersRole(userId = user.id, roleId = hodorAdminRole.id))


        val user =
            appRolesRepository
                .findByAppAndName(app, hodorUserRole.name)

        hodorUserRole = user
            ?: appRolesRepository.create(
            hodorUserRole.copy(appId = app.id))


        hodorRoles = listOf(hodorAdminRole, hodorUserRole)
    }
}