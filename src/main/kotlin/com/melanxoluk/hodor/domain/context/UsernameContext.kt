package com.melanxoluk.hodor.domain.context

import com.melanxoluk.hodor.domain.entities.*


open class UsernameContext(
    val usernamePassword: UsernamePassword,
    app: App,
    client: AppClient,
    userRolesContext: UserRolesContext)
    : UserContext(app, client, userRolesContext) {

    val username get() = usernamePassword.username
    val password get() = usernamePassword.password
}
