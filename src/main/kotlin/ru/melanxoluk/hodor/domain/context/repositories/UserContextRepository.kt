package ru.melanxoluk.hodor.domain.context.repositories

import ru.melanxoluk.hodor.common.UsernameLogin
import ru.melanxoluk.hodor.domain.context.UserContext
import ru.melanxoluk.hodor.secure.TokenService
import org.koin.core.component.get

class UserContextRepository: ContextRepository() {
    private val usersRolesContextRepository = get<UsersRolesContextRepository>()
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

    fun get(login: UsernameLogin): UserContext? {
        // fixme
        val client = clientsRepository.findByUuid(login.client).getOrThrow()
            ?: return null

        // client uuid -> client -> app
        // username -> usernamePassword -> user
        // user -> user roles -> app roles
        val app = appsRepository.read(client.id)

        // fixme
        val usernamePassword = usernamePasswordsRepository.findByUsername(login.username).getOrThrow()
        val user = usersRepository.read(usernamePassword.id)
        val userRolesContext = usersRolesContextRepository.get(user)
        return UserContext(app, client, userRolesContext)
    }
}