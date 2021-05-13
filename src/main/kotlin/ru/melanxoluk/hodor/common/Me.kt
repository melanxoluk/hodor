package ru.melanxoluk.hodor.common

import ru.melanxoluk.hodor.domain.context.UserContext
import ru.melanxoluk.hodor.domain.entities.User
import java.util.*

data class Me(
    val uuid: UUID,
    val username: String,
    val data: String) {

    constructor(user: User): this(user.uuid, user.username, user.properties)
    constructor(user: UserContext): this(user.user.uuid, user.username, user.user.properties)
}