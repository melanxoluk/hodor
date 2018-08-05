package com.melanxoluk.hodor.server.storage

import com.melanxoluk.hodor.allNotNull
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
//   when after first starting application & creating that entities
//   such uuids makes lost and run application again


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
    Application(
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
    private val applicationsRepository = get<ApplicationsRepository>()
    private val appClientsRepository = get<AppClientsRepository>()
    private val appRolesRepository = get<AppRolesRepository>()
    private val usersRepository = get<UsersRepository>()


    fun initialize(databaseProperties: DatabaseProperties) {
        val url = String.format(postgresJdbcTemplate, databaseProperties.name)
        Database.connect(
            url,
            Driver::class.jvmName,
            databaseProperties.user,
            databaseProperties.password)

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
            val client = appClientsRepository.create(hodorClient)

            TransactionManager.current().exec("ALTER TABLE users ADD FOREIGN KEY (application) REFERENCES applications(id) ON DELETE RESTRICT")
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

    private fun initHodorAppClient(app: Application) {
        val client =
            appClientsRepository
                .findByApp(app)

        hodorClient = client
            ?: appClientsRepository.create(
                hodorClient.copy(appId = app.id))
    }

    private fun initHodorAppRoles(user: User, app: Application) {
        val admin =
            appRolesRepository
                .findByAppAndName(app, hodorAdminRole.name)

        hodorAdminRole = admin
            ?: appRolesRepository.create(
                hodorAdminRole.copy(appId = app.id))


        val user =
            appRolesRepository
                .findByAppAndName(app, hodorUserRole.name)

        hodorUserRole = user
            ?: appRolesRepository.create(
                hodorUserRole.copy(appId = app.id))



    }
}