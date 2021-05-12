package ru.melanxoluk.hodor.services

import ru.melanxoluk.hodor.common.UsernameLogin
import ru.melanxoluk.hodor.domain.context.UsernameContext
import ru.melanxoluk.hodor.domain.entities.User
import ru.melanxoluk.hodor.domain.entities.UsernamePassword
import ru.melanxoluk.hodor.domain.entities.repositories.AppClientsRepository
import ru.melanxoluk.hodor.domain.entities.repositories.UsernamePasswordsRepository
import ru.melanxoluk.hodor.domain.entities.repositories.UsersRepository
import ru.melanxoluk.hodor.secure.PasswordHasher
import org.koin.core.component.get
import java.util.*


class UsersService: Service() {
    private val usernamePasswordsRepository = get<UsernamePasswordsRepository>()
    private val clientsRepository = get<AppClientsRepository>()
    private val passwordHasher = get<PasswordHasher>()

    fun getOrCreate(login: UsernameLogin): UsernamePassword {
        return get(login) ?: create(login)
    }

    fun get(login: UsernameLogin): UsernamePassword? {
        // find client by uuid
        // fixme
        val client = clientsRepository.findByUuid(login.client).getOrNull()
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