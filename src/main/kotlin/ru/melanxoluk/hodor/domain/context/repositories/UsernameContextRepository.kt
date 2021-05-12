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


class UsernameContextRepository: ContextRepository() {
    private companion object {
        private fun notFoundClient(uuid: UUID) = "Not found client $uuid"
        private fun notFoundUser(username: String) = "Not found user with username $username"
    }


    private val usersRolesContextRepository = get<UsersRolesContextRepository>()
    private val appContextRepository = get<AppContextRepository>()
    private val passwordsHasher = get<PasswordHasher>()


    fun getOrCreate(login: UsernameLogin): Result<UsernameContext> {
        val client = clientsRepository.findByUuid(login.client)
            ?: return negative(notFoundClient(login.client))

        val res = get(client, login)
        return when(res) {
            is Positive -> res
            is Negative -> create(client, login)
            else -> throw IllegalStateException()
        }
    }

    fun get(login: UsernameLogin): Result<UsernameContext> {
        val client = clientsRepository.findByUuid(login.client)
            ?: return negative(notFoundClient(login.client))

        return get(client, login)
    }

    fun get(client: AppClient, login: UsernameLogin): Result<UsernameContext> {
        val usernamePassword = usernamePasswordsRepository.findByUsername(login.username)
            ?: return negative(notFoundUser(login.username))

        val app = appsRepository.read(client.id)
        val user = usersRepository.read(usernamePassword.id)
        val userRolesContext = usersRolesContextRepository.get(user)
        return positive(UsernameContext(usernamePassword, app, client, userRolesContext))
    }

    fun create(login: UsernameLogin): Result<UsernameContext> {
        val client = clientsRepository.findByUuid(login.client)
            ?: return negative(notFoundClient(login.client))

        return create(client, login)
    }

    fun create(client: AppClient, login: UsernameLogin): Result<UsernameContext> {
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
        return positive(UsernameContext(usernamePassword, appContext.app, client, userRolesContext))
    }
}
