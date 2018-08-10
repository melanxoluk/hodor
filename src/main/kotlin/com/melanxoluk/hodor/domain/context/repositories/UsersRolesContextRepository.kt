package com.melanxoluk.hodor.domain.context.repositories

import com.melanxoluk.hodor.domain.context.UserRolesContext
import com.melanxoluk.hodor.domain.entities.User


class UsersRolesContextRepository: ContextRepository() {
    fun get(user: User): UserRolesContext {
        val userRoles = userRolesRepository.findByUser(user)
        val appRoles = appRolesRepository.findByUserRoles(userRoles)
        return UserRolesContext(user, userRoles, appRoles)
    }
}