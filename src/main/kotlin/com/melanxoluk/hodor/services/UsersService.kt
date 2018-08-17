package com.melanxoluk.hodor.services

import com.melanxoluk.hodor.common.UsernameLogin
import com.melanxoluk.hodor.domain.context.UsernameContext
import com.melanxoluk.hodor.domain.entities.AppClient
import com.melanxoluk.hodor.domain.entities.User
import com.melanxoluk.hodor.domain.entities.UsernamePassword
import com.melanxoluk.hodor.secure.PasswordHasher
import com.melanxoluk.hodor.domain.entities.repositories.AppClientsRepository
import com.melanxoluk.hodor.domain.entities.repositories.AppsRepository
import com.melanxoluk.hodor.domain.entities.repositories.UsernamePasswordsRepository
import com.melanxoluk.hodor.domain.entities.repositories.UsersRepository
import org.koin.standalone.get
import java.util.*


class UsersService: Service() {
    private val usernamePasswordsRepository = get<UsernamePasswordsRepository>()
    private val clientsRepository = get<AppClientsRepository>()
    private val usersRepository = get<UsersRepository>()
    private val appsRepository = get<AppsRepository>()
    private val passwordHasher = get<PasswordHasher>()

    fun getOrCreate(login: UsernameLogin): UsernamePassword {
        return get(login) ?: create(login)
    }

    fun get(login: UsernameLogin): UsernamePassword? {
        // find client by uuid
        val client = clientsRepository.findByUuid(login.client)
            ?: return null

        // find user by app_id in client with username
        val pair = usersRepository.findByAppAndEmail(
            client.appId, login.username) ?: return null

        return pair.first.apply { user = pair.second }
    }

    fun create(login: UsernameLogin): UsernamePassword {
        val app = appsRepository.findByClientUuid(login.client)
            ?: throw IllegalArgumentException(
                "Not found app with client ${login.client}")

        val user = usersRepository.create(
            User(0L, app.id, "", UUID.randomUUID()))

        val passHash = passwordHasher.hash(login.password)
        val usernamePass = usernamePasswordsRepository.create(
            UsernamePassword(0, login.username, passHash, user.id))

        return usernamePass.apply { this.user = user }
    }

    fun create2(login: UsernameLogin): UsernameContext {
        // determined that
        TODO()
    }
}