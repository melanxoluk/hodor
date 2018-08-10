package com.melanxoluk.hodor.domain.context.repositories

import com.melanxoluk.hodor.common.UsernameLogin
import com.melanxoluk.hodor.domain.context.UserContext
import org.koin.standalone.get


class UserContextRepository: ContextRepository() {
    private val usersRolesContextRepository = get<UsersRolesContextRepository>()

    fun get(login: UsernameLogin): UserContext? {
        val client = clientsRepository.findByUuid(login.client)
            ?: return null

        // client uuid -> client -> app
        // username -> usernamePassword -> user
        // user -> user roles -> app roles
        val app = appsRepository.read(client.id)!!
        val usernamePassword = usernamePasswordsRepository.findByUsername(login.username)!!
        val user = usersRepository.read(usernamePassword.id)!!
        val userRolesContext = usersRolesContextRepository.get(user)
        return UserContext(app, client, userRolesContext)
    }
}