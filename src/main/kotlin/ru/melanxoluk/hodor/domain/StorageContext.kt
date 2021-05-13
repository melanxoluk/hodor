package ru.melanxoluk.hodor.domain

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ru.melanxoluk.hodor.domain.entities.*
import ru.melanxoluk.hodor.domain.entities.repositories.*
import ru.melanxoluk.hodor.domain.entities.repositories.AppClientsRepository.AppClientsTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppCreatorsRepository.AppCreatorTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppRolesRepository.AppRolesTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppsRepository.AppTable
import ru.melanxoluk.hodor.domain.entities.repositories.DefaultAppRolesRepository.DefaultAppRolesTable
import ru.melanxoluk.hodor.domain.entities.repositories.UserRolesRepository.UserRolesTable
import ru.melanxoluk.hodor.domain.entities.repositories.UsersRepository.UsersTable
import ru.melanxoluk.hodor.secure.PasswordHasher


// todo: don't like such initialization flow, but don't know how to make better
object StorageContext: KoinComponent {
    private const val postgresJdbcTemplate = "jdbc:postgresql:%s"

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
            initHodorAppClient(hodorApp)
            initHodorAppRoles(hodorSuperUser, hodorApp)
        }
    }


    // ~~~ init app entities

    private fun initHodorApp() {
        applicationsRepository.findByName(hodorApp.name)
            .onSuccess { hodorApp = it }
            .onFailure { hodorApp = applicationsRepository.create(hodorApp) }
    }

    private fun initHodorUser(app: App) {
        usersRepository.findWithHodorPrefix()
            .onFailure {
                hodorSuperUser = usersRepository.create(
                    hodorSuperUser.copy(appId = app.id)
                )

                appCreatorsRepository.create(
                    AppCreator(appId = app.id, userId = hodorSuperUser.id)
                )
            }
            .onSuccess { hodorSuperUser = it }
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
