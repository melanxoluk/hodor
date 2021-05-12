package ru.melanxoluk.hodor.services

import ru.melanxoluk.hodor.domain.context.AppContext
import ru.melanxoluk.hodor.domain.context.UserContext
import ru.melanxoluk.hodor.domain.entities.*
import java.util.*


class AppsService: Service() {
    fun getAll(userContext: UserContext) = ok<List<AppContext>> {
        appContextRepository.getAll(userContext)
    }

    fun create(userContext: UserContext, name: String) = ok<AppContext> {
        val app = appsRepository.create(App(0, name, UUID.randomUUID()))
        val appClient = appClientsRepository.create(AppClient(0, app.id, "web", UUID.randomUUID()))

        val creator = appCreatorsRepository.create(AppCreator(0, app.id, userContext.userId))
        val adminRole = appRoleRepository.create(AppRole(0, UUID.randomUUID(), app.id, "admin"))
        val userRole = appRoleRepository.create(AppRole(0, UUID.randomUUID(), app.id, "user"))
        defaultAppRolesRepository.create(DefaultAppRole(0, app.id, userRole.id))

        // first user of application is creator with admin role
        val admin = usersRepository.create(User(0, app.id, "", UUID.randomUUID()))
        userRolesRepository.create(UserRole(0, adminRole.id, admin.id))

        return@ok AppContext(app, userContext.user, creator, listOf(userRole), listOf(adminRole, userRole))
    }
}
