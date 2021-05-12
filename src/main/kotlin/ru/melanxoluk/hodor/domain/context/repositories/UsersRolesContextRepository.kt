package ru.melanxoluk.hodor.domain.context.repositories

import ru.melanxoluk.hodor.domain.context.UserRolesContext
import ru.melanxoluk.hodor.domain.entities.User


class UsersRolesContextRepository: ContextRepository() {
    fun get(user: User): UserRolesContext {
        val userRoles = userRolesRepository.findByUser(user)
        val appRoles = appRolesRepository.findByUserRoles(userRoles)
        return UserRolesContext(user, userRoles, appRoles)
    }
}