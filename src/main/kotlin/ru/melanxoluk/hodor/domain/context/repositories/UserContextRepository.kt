package ru.melanxoluk.hodor.domain.context.repositories

import ru.melanxoluk.hodor.common.UsernameLogin
import ru.melanxoluk.hodor.domain.context.UserContext
import ru.melanxoluk.hodor.secure.TokenService
import org.koin.core.component.get
import ru.melanxoluk.hodor.common.flatMap
import ru.melanxoluk.hodor.domain.context.UserRolesContext
import ru.melanxoluk.hodor.domain.entities.AppClient
import ru.melanxoluk.hodor.domain.entities.User
import ru.melanxoluk.hodor.domain.entities.UserRole
import java.util.*


// todo: move to UsersService all none persisting logic
class UserContextRepository: ContextRepository() {
    private companion object {
        private fun notFoundClient(uuid: UUID) = "Not found client $uuid"
        private fun notFoundUser(username: String) = "Not found user with username $username"
    }
    
    private val usersRolesContextRepository = get<UsersRolesContextRepository>()
    private val appContextRepository = get<AppContextRepository>()
    private val tokenService = get<TokenService>()

    
    fun get(token: String): UserContext {
        // token should be verified already here
        // extract all available ids from token
        val parsedToken = tokenService.parse(token)

        // fixme
        val client = clientsRepository.findByUuid(parsedToken.clientUuid).getOrThrow()
        val app = appsRepository.findByUuid(parsedToken.appUuid)!!

        val user = usersRepository.findByUuid(parsedToken.userUuid)!!
        val userRolesContext = usersRolesContextRepository.get(user)

        return UserContext(app, client, userRolesContext)
    }

    fun get(login: UsernameLogin): Result<UserContext> {
        return clientsRepository.findByUuid(login.client).flatMap {
            get(it, login)
        }
    }

    fun get(client: AppClient, login: UsernameLogin): Result<UserContext> {
        return usersRepository.findByUsername(client.appId, login.username).map { user ->
            val app = appsRepository.read(client.id)
            val userRolesContext = usersRolesContextRepository.get(user)
            UserContext(app, client, userRolesContext)
        }
    }


    fun create(login: UsernameLogin): Result<UserContext> {
        return clientsRepository.findByUuid(login.client).map {
            create(it, login)
        }
    }

    fun create(client: AppClient, login: UsernameLogin): UserContext {
        val appContext = appContextRepository.get(client)

        val pass = passwordHasher.hash(login.password)
        val user = usersRepository.create(
            User(0, appContext.app.id, login.username, pass, "", UUID.randomUUID())
        )

        val userRoles = appContext.defaultRoles.map { appRole ->
            userRolesRepository.create(UserRole(0, appRole.id, user.id))
        }

        return UserContext(
            appContext.app,
            client,
            UserRolesContext(
                user,
                userRoles,
                appContext.defaultRoles))
    }
}