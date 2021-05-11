package com.melanxoluk.hodor.domain.context.repositories

import com.melanxoluk.hodor.domain.entities.repositories.*
import com.melanxoluk.hodor.secure.PasswordHasher
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


abstract class ContextRepository: KoinComponent {
    protected val usernamePasswordsRepository = get<UsernamePasswordsRepository>()
    protected val defaultAppRolesRepository = get<DefaultAppRolesRepository>()
    protected val appCreatorsRepository = get<AppCreatorsRepository>()
    protected val userRolesRepository = get<UserRolesRepository>()
    protected val appRolesRepository = get<AppRolesRepository>()
    protected val clientsRepository = get<AppClientsRepository>()
    protected val usersRepository = get<UsersRepository>()
    protected val appsRepository = get<AppsRepository>()
    protected val passwordHasher = get<PasswordHasher>()
}