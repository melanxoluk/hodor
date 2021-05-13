package ru.melanxoluk.hodor.services

import org.koin.core.component.get
import ru.melanxoluk.hodor.common.Me
import ru.melanxoluk.hodor.common.UserAuth
import ru.melanxoluk.hodor.common.UsernameLogin
import ru.melanxoluk.hodor.secure.TokenService


class UsersService: Service() {
    private val tokenService = get<TokenService>()


    fun register(login: UsernameLogin) =
        userContextRepository.create(login).map { user ->
            UserAuth(Me(user), tokenService.generate(user))
        }

    fun getMe(token: String) =
        get(token).map(::Me)

    fun get(token: String) =
        tokenService.parse(token).map {
            userContextRepository.get(it)
        }

    fun update(request: Me): Result<Me> {
        return usersRepository.findByUuid(request.uuid).map { user ->
            Me(usersRepository.update(user.copy(properties = request.data)))
        }
    }
}