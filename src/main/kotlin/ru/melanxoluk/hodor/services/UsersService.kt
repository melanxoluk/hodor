package ru.melanxoluk.hodor.services

import org.koin.core.component.get
import ru.melanxoluk.hodor.common.UsernameLogin
import ru.melanxoluk.hodor.domain.entities.User
import ru.melanxoluk.hodor.domain.entities.repositories.AppClientsRepository
import ru.melanxoluk.hodor.secure.PasswordHasher
import java.util.*


class UsersService: Service() {
    private val clientsRepository = get<AppClientsRepository>()
    private val passwordHasher = get<PasswordHasher>()

    fun getOrCreate(login: UsernameLogin): User {
        return get(login) ?: create(login)
    }

    fun get(login: UsernameLogin): User? {
        // find client by uuid
        // fixme
        val client = clientsRepository.findByUuid(login.client).getOrNull()
            ?: return null

        // find user by app_id in client with username
        return usersRepository.findByAppAndEmail(client.appId, login.username).getOrNull()
    }

    fun create(login: UsernameLogin): User {
        val app = appsRepository.findByClientUuid(login.client)
            ?: throw IllegalArgumentException(
                "Not found app with client ${login.client}"
            )

        val passHash = passwordHasher.hash(login.password)

        return usersRepository.create(
            User(0L, app.id, "", login.username, passHash, UUID.randomUUID())
        )
    }
}