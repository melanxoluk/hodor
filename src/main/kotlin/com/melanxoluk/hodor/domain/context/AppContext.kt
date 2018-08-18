package com.melanxoluk.hodor.domain.context

import com.melanxoluk.hodor.domain.entities.App
import com.melanxoluk.hodor.domain.entities.AppCreator
import com.melanxoluk.hodor.domain.entities.AppRole
import com.melanxoluk.hodor.domain.entities.User


class AppContext(
    val app: App,
    val creator: User,
    val appCreator: AppCreator,
    val defaultRoles: List<AppRole>,
    val roles: List<AppRole>)
