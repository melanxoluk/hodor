package ru.melanxoluk.hodor.domain

import ru.melanxoluk.hodor.DatabaseProperties
import ru.melanxoluk.hodor.domain.entities.*
import ru.melanxoluk.hodor.domain.entities.repositories.*
import ru.melanxoluk.hodor.domain.entities.repositories.AppClientsRepository.AppClientsTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppCreatorsRepository.AppCreatorTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppRolesRepository.AppRolesTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppsRepository.AppTable
import ru.melanxoluk.hodor.domain.entities.repositories.DefaultAppRolesRepository.DefaultAppRolesTable
import ru.melanxoluk.hodor.domain.entities.repositories.UserRolesRepository.UserRolesTable
import ru.melanxoluk.hodor.domain.entities.repositories.UsernamePasswordsRepository.UsernamePasswordTable
import ru.melanxoluk.hodor.domain.entities.repositories.UsersRepository.UsersTable
import ru.melanxoluk.hodor.secure.PasswordHasher
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
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
    private val usersRolesRepository = get<UserRolesRepository>()
    private val appRolesRepository = get<AppRolesRepository>()
    private val usersRepository = get<UsersRepository>()
    private val hasher = get<PasswordHasher>()

    fun initialize(databaseProperties: ru.melanxoluk.hodor.DatabaseProperties) {
        val url = String.format(postgresJdbcTemplate, databaseProperties.name)
        Database.connect(
            url = url,
            user = databaseProperties.user,
            password = databaseProperties.password)

        // todo:
        //   need validation mechanism to check exists database scheme
        //   with current
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                AppTable,
                AppClientsTable,
                AppRolesTable,
                UsernamePasswordTable,
                UsersTable,
                DefaultAppRolesTable,
                AppCreatorTable,
                UserRolesTable
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
        } else {
            hodorSuperUser = hodorUser
        }
    }

    private fun initHodorUsernamePass(user: User) {
        val usernamePassword =
            usernamePasswordsRepository
                .findByUser(user)

        hodorSuperUsernamePassword = usernamePassword
            ?: usernamePasswordsRepository.create(
                hodorSuperUsernamePassword.copy(
                    userId = user.id,
                    password = hasher.hash(
                        hodorSuperUsernamePassword.password)))
    }

    private fun initHodorAppClient(app: App) {
        appClientsRepository
            .findByAppAndType(app, hodorClient.type)
            .onSuccess { client -> hodorClient = client }
            .onFailure { hodorClient = appClientsRepository.create(hodorClient.copy(appId = app.id)) }
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
        } else {
            hodorUserRole = userRole
        }

        hodorRoles = listOf(hodorAdminRole, hodorUserRole)
    }
}
