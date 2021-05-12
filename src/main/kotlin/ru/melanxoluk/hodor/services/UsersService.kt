package ru.melanxoluk.hodor.services

import ru.melanxoluk.hodor.common.UsernameLogin
import ru.melanxoluk.hodor.domain.context.UserContext
import ru.melanxoluk.hodor.domain.entities.User


class UsersService: Service() {
    fun create(login: UsernameLogin): Result<User> {
        return userContextRepository.create(login).map(UserContext::user)
    }
}