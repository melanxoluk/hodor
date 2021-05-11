package com.melanxoluk.hodor.domain.context.repositories

import com.melanxoluk.hodor.common.UsernameLogin
import com.melanxoluk.hodor.domain.context.UserContext
import com.melanxoluk.hodor.secure.TokenService
import org.koin.core.component.get

class UserContextRepository: ContextRepository() {
    private val usersRolesContextRepository = get<UsersRolesContextRepository>()
    private val tokenService = get<TokenService>()

    fun get(token: String): UserContext {
        // token should be verified already here
        // extract all available ids from token
        val parsedToken = tokenService.parse(token)

        val client = clientsRepository.findByUuid(parsedToken.clientUuid)!!
        val app = appsRepository.findByUuid(parsedToken.appUuid)!!

        val user = usersRepository.findByUuid(parsedToken.userUuid)!!
        val userRolesContext = usersRolesContextRepository.get(user)

        return UserContext(app, client, userRolesContext)
    }

    fun get(login: UsernameLogin): UserContext? {
        val client = clientsRepository.findByUuid(login.client)
            ?: return null

        // client uuid -> client -> app
        // username -> usernamePassword -> user
        // user -> user roles -> app roles
        val app = appsRepository.read(client.id)
        val usernamePassword = usernamePasswordsRepository.findByUsername(login.username)!!
        val user = usersRepository.read(usernamePassword.id)
        val userRolesContext = usersRolesContextRepository.get(user)
        return UserContext(app, client, userRolesContext)
    }
}