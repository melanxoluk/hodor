package com.melanxoluk.hodor.common

import com.melanxoluk.hodor.domain.entities.App
import com.melanxoluk.hodor.domain.entities.AppClient
import com.melanxoluk.hodor.domain.entities.AppRole
import com.melanxoluk.hodor.domain.entities.User


open class RequestContext(
    val roles: List<AppRole>,
    val client: AppClient,
    val user: User,
    val app: App) {

    val clientUuid get() = client.uuid
    val clientId get() = client.id

    val userUuid get() = user.uuid
    val userId get() = user.id

    val appUuid get() = app.uuid
    val appId get() = app.id
}
