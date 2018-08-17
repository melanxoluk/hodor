package com.melanxoluk.hodor.domain.context.repositories

import com.melanxoluk.hodor.domain.context.AppContext
import com.melanxoluk.hodor.domain.entities.AppClient


class AppContextRepository: ContextRepository() {
    fun get(client: AppClient): AppContext {
        val app = appsRepository.read(client.id)
        val defaultAppRoles = defaultAppRolesRepository.findByApp(app)
        val appRoles = appRolesRepository.findByAppDefaultRoles(defaultAppRoles)
        val appCreator = appCreatorsRepository.findByApp(app)!!
        val user = usersRepository.read(appCreator.userId)
        return AppContext(app, user, appCreator, appRoles)
    }
}