package com.melanxoluk.hodor.server.storage

import com.melanxoluk.hodor.domain.*
import com.melanxoluk.hodor.server.DatabaseProperties
import com.melanxoluk.hodor.server.HodorConfig
import com.melanxoluk.hodor.server.storage.repositories.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.postgresql.Driver
import java.util.*
import kotlin.reflect.jvm.jvmName


// fixme:
//   stop to embed uuids in sources. Decide how to handle situation
//   when after first starting app & creating that entities
//   such uuids makes lost and run app again


// fixme:
//   hodor entities are belong to hodor user, user who's properties
//   are hodor prefix. No more users should be able to has such properties
const val hodorPrefix = "__hodor__"


// ~~~ super user

var hodorSuperUsernamePassword =
    UsernamePassword(
        id = 0,
        username = HodorConfig.superUser.login,
        password = HodorConfig.superUser.password,
        userId = 0)
    private set

var hodorSuperUser =
    User(
        id = 0,
        appId = 0,
        properties = hodorPrefix,
        uuid = UUID.fromString("4ccd0b37-a0d0-423c-8a2f-796d85ee8528"))
    private set


// ~~~ hodor app

var hodorApp =
    App(
        id = 0,
        creatorId = 0,
        name = hodorPrefix + "Hodor",
        uuid = UUID.fromString("036b0274-f6e5-4721-a5f6-fdf0efbb8e3f"))
    private set

var hodorClient =
    AppClient(
        id = 0,
        appId = 0,
        type = "web",
        uuid = UUID.fromString("fd1c662f-b196-43a6-a914-368458c1bb83"))
    private set


var hodorAdminRole =
    AppRole(
        id = 0,
        appId = 0,
        uuid = UUID.fromString("9bf49948-7100-4fb6-977a-ec26cf9e8820"),
        name = "admin")
    private set

var hodorUserRole =
    AppRole(
        id = 0,
        appId = 0,
        uuid = UUID.fromString("daf5b91b-e41f-4e2a-8b71-ace61c7dbf65"),
        name = "user"
    )
    private set

var hodorRoles = listOf(hodorAdminRole, hodorUserRole)



// ~~~ logic to initialize storage context

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