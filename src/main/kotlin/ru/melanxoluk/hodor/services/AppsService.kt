package ru.melanxoluk.hodor.services

import ru.melanxoluk.hodor.domain.context.AppContext
import ru.melanxoluk.hodor.domain.context.UserContext
import ru.melanxoluk.hodor.domain.entities.*
import java.util.*


class AppsService: Service() {
    fun getAll(userContext: UserContext) =
        appContextRepository.getAll(userContext)

    fun create(context: UserContext, name: String): AppContext {
        val app = appsRepository.create(App(0, name, UUID.randomUUID()))

        // todo I don't sure about now
        val webClient = appClientsRepository.create(AppClient(0, app.id, "web", UUID.randomUUID()))

        val creator = appCreatorsRepository.create(AppCreator(0, app.id, context.userId))
        val adminRole = appRoleRepository.create(AppRole(0, UUID.randomUUID(), app.id, "admin"))
        val userRole = appRoleRepository.create(AppRole(0, UUID.randomUUID(), app.id, "user"))
        defaultAppRolesRepository.create(DefaultAppRole(0, app.id, userRole.id))

        // first user of application is creator with admin role
        val admin = usersRepository.create(User(0, app.id, context.username, context.password, "", UUID.randomUUID()))
        userRolesRepository.create(UserRole(0, adminRole.id, admin.id))

        return AppContext(app, context.user, creator, listOf(userRole), listOf(adminRole, userRole), listOf(webClient))
    }
}
