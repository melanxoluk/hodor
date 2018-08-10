package com.melanxoluk.hodor.common

import com.melanxoluk.hodor.domain.entities.*


open class UsernameContext(
    val usernamePassword: UsernamePassword,
    roles: List<AppRole>,
    client: AppClient,
    user: User,
    app: App)
    : RequestContext(roles, client, user, app) {

    val username get() = usernamePassword.username
    val password get() = usernamePassword.password
}