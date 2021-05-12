package ru.melanxoluk.hodor.domain.context

import ru.melanxoluk.hodor.domain.entities.AppRole
import ru.melanxoluk.hodor.domain.entities.User
import ru.melanxoluk.hodor.domain.entities.UserRole


open class UserRolesContext(
    val user: User,
    val userRoles: List<UserRole>,
    val appRoles: List<AppRole>)