package ru.melanxoluk.hodor.domain.context

import ru.melanxoluk.hodor.domain.entities.*


open class UserContext(
    val client: AppClient,
    val app: App,
    userRoles: List<UserRole>,
    appRoles: List<AppRole>,
    user: User)
    : UserRolesContext(user, userRoles, appRoles) {

    constructor(app: App,
                client: AppClient,
                userRolesContext: UserRolesContext) : this(
        client,
        app,
        userRolesContext.userRoles,
        userRolesContext.appRoles,
        userRolesContext.user)


    val clientUuid get() = client.uuid
    val clientId get() = client.id

    val userUuid get() = user.uuid
    val username get() = user.username
    val password get() = user.password
    val userId get() = user.id

    val appUuid get() = app.uuid
    val appId get() = app.id
}
