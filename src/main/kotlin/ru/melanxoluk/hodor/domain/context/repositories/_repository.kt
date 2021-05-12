package ru.melanxoluk.hodor.domain.context.repositories

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ru.melanxoluk.hodor.domain.entities.repositories.*
import ru.melanxoluk.hodor.secure.PasswordHasher


abstract class ContextRepository: KoinComponent {
    protected val defaultAppRolesRepository = get<DefaultAppRolesRepository>()
    protected val appCreatorsRepository = get<AppCreatorsRepository>()
    protected val userRolesRepository = get<UserRolesRepository>()
    protected val appRolesRepository = get<AppRolesRepository>()
    protected val clientsRepository = get<AppClientsRepository>()
    protected val usersRepository = get<UsersRepository>()
    protected val appsRepository = get<AppsRepository>()
    protected val passwordHasher = get<PasswordHasher>()
}