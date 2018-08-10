package com.melanxoluk.hodor.domain

import com.melanxoluk.hodor.domain.entities.*
import com.melanxoluk.hodor.domain.entities.repositories.*
import com.melanxoluk.hodor.domain.entities.repositories.AppClientsRepository.AppClientsTable
import com.melanxoluk.hodor.domain.entities.repositories.AppCreatorsRepository.AppCreatorTable
import com.melanxoluk.hodor.domain.entities.repositories.AppRolesRepository.AppRolesTable
import com.melanxoluk.hodor.domain.entities.repositories.AppsRepository.ApplicationsTable
import com.melanxoluk.hodor.domain.entities.repositories.DefaultAppRolesRepository.DefaultAppRolesTable
import com.melanxoluk.hodor.domain.entities.repositories.UsernamePasswordsRepository.UsernamePasswordTable
import com.melanxoluk.hodor.domain.entities.repositories.UsersRepository.UsersTable
import com.melanxoluk.hodor.domain.entities.repositories.UsersRolesRepository.UsersRolesTable
import com.melanxoluk.hodor.server.DatabaseProperties
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.postgresql.Driver
import kotlin.reflect.jvm.jvmName


// todo: don't like such initialization flow, but don't know how to make better
object StorageContext: KoinComponent {
    private const val postgresJdbcTemplate = "jdbc:postgresql:%s"

    private val usernamePasswordsRepository = get<UsernamePasswordsRepository>()
    private val defaultAppRolesRepository = get<DefaultAppRolesRepository>()
    private val applicationsRepository = get<AppsRepository>()
    private val appCreatorsRepository = get<AppCreatorsRepository>()
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

        // todo:
        //   need validation mechanism to check exists database scheme
        //   with current
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                UsernamePasswordTable,
                DefaultAppRolesTable,
                ApplicationsTable,
                AppCreatorTable,
                AppClientsTable,
                UsersRolesTable,
                AppRolesTable,
                UsersTable
            )
        }

        // refresh hodor app entities
        transaction {
            initHodorApp()
            initHodorUser(hodorApp)
            initHodorUsernamePass(hodorSuperUser)
            initHodorAppClient(hodorApp)
            initHodorAppRoles(hodorSuperUser, hodorApp)
        }
    }


    // ~~~ init app entities

    private fun initHodorApp() {
        val app =
            applicationsRepository
                .findByName(hodorApp.name)

        hodorApp = app ?:
            applicationsRepository.create(hodorApp)
    }

    private fun initHodorUser(app: App) {
        val hodorUser =
            usersRepository
                .findWithHodorPrefix()

        if (hodorUser == null) {
            hodorSuperUser = usersRepository.create(
                hodorSuperUser.copy(appId = app.id))

            appCreatorsRepository.create(
                AppCreator(appId = app.id, userId = hodorSuperUser.id))
        }
    }

    private fun initHodorUsernamePass(user: User) {
        val usernamePassword =
            usernamePasswordsRepository
                .findByUser(user)

        hodorSuperUsernamePassword = usernamePassword
            ?: usernamePasswordsRepository.create(
                hodorSuperUsernamePassword.copy(userId = user.id))
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
                UserRole(userId = user.id, roleId = hodorAdminRole.id))


        val userRole =
            appRolesRepository
                .findByAppAndName(app, hodorUserRole.name)

        if (userRole == null) {
            hodorUserRole = appRolesRepository.create(
                hodorUserRole.copy(appId = app.id))

            defaultAppRolesRepository.create(
                DefaultAppRole(appId = app.id, roleId = hodorUserRole.id))
        }

        hodorRoles = listOf(hodorAdminRole, hodorUserRole)
    }
}