package com.melanxoluk.hodor.domain.context

import com.melanxoluk.hodor.domain.entities.AppRole
import com.melanxoluk.hodor.domain.entities.User
import com.melanxoluk.hodor.domain.entities.UserRole


open class UserRolesContext(
    val user: User,
    val userRoles: List<UserRole>,
    val appRoles: List<AppRole>)