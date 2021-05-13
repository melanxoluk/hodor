package ru.melanxoluk.hodor.domain.context

import ru.melanxoluk.hodor.domain.entities.*


data class AppContext(
    val app: App,
    val creator: User,
    val appCreator: AppCreator,
    val defaultRoles: List<AppRole>,
    val roles: List<AppRole>,
    val clients: List<AppClient>)
