package ru.melanxoluk.hodor.domain.context.repositories

import ru.melanxoluk.hodor.common.*
import ru.melanxoluk.hodor.domain.context.UserRolesContext
import ru.melanxoluk.hodor.domain.context.UsernameContext
import ru.melanxoluk.hodor.domain.entities.AppClient
import ru.melanxoluk.hodor.domain.entities.User
import ru.melanxoluk.hodor.domain.entities.UserRole
import ru.melanxoluk.hodor.domain.entities.UsernamePassword
import ru.melanxoluk.hodor.secure.PasswordHasher
import org.koin.core.component.get
import java.util.*
import kotlin.math.log


class UsernameContextRepository: ContextRepository() {
    private companion object {
        private fun notFoundClient(uuid: UUID) = "Not found client $uuid"
        private fun notFoundUser(username: String) = "Not found user with username $username"
    }


    private val usersRolesContextRepository = get<UsersRolesContextRepository>()
    private val appContextRepository = get<AppContextRepository>()
    private val passwordsHasher = get<PasswordHasher>()


    fun getOrCreate(login: UsernameLogin): Result<UsernameContext> {
        return clientsRepository.findByUuid(login.client).fold(
            { get(it, login) },
            { create(login) }
        )
    }

    fun get(login: UsernameLogin): Result<UsernameContext> {
        return clientsRepository.findByUuid(login.client).flatMap {
            get(it, login)
        }
    }

    fun get(client: AppClient, login: UsernameLogin): Result<UsernameContext> {
        return usernamePasswordsRepository.findByUsername(login.username).map { usernamePassword ->
            val app = appsRepository.read(client.id)
            val user = usersRepository.read(usernamePassword.id)
            val userRolesContext = usersRolesContextRepository.get(user)
            UsernameContext(usernamePassword, app, client, userRolesContext)
        }
    }

    fun create(login: UsernameLogin): Result<UsernameContext> {
        return clientsRepository.findByUuid(login.client).map {
            create(it, login)
        }
    }

    fun create(client: AppClient, login: UsernameLogin): UsernameContext {
        val appContext = appContextRepository.get(client)
        val user = usersRepository.create(
            User(0, appContext.app.id, "", UUID.randomUUID()))

        val pass = passwordsHasher.hash(login.password)
        val usernamePassword  = usernamePasswordsRepository.create(
            UsernamePassword(0, login.username, pass, user.id))

        val userRoles = appContext.defaultRoles.map { appRole ->
            userRolesRepository.create(UserRole(0, appRole.id, user.id))
        }

        val userRolesContext = UserRolesContext(user, userRoles, appContext.defaultRoles)
        return UsernameContext(usernamePassword, appContext.app, client, userRolesContext)
    }
}
