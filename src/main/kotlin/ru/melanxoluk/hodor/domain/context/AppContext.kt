package ru.melanxoluk.hodor.domain.context

import ru.melanxoluk.hodor.domain.entities.App
import ru.melanxoluk.hodor.domain.entities.AppCreator
import ru.melanxoluk.hodor.domain.entities.AppRole
import ru.melanxoluk.hodor.domain.entities.User


class AppContext(
    val app: App,
    val creator: User,
    val appCreator: AppCreator,
    val defaultRoles: List<AppRole>,
    val roles: List<AppRole>)
