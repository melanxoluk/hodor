package com.melanxoluk.hodor.domain.context.repositories

import com.melanxoluk.hodor.domain.context.AppContext
import com.melanxoluk.hodor.domain.context.UserContext
import com.melanxoluk.hodor.domain.entities.App
import com.melanxoluk.hodor.domain.entities.AppClient
import com.melanxoluk.hodor.domain.entities.AppCreator


class AppContextRepository: ContextRepository() {
    fun getAll(userContext: UserContext): List<AppContext> {
        val appCreatorEntries = appCreatorsRepository.findByUser(userContext.user)
        return appCreatorEntries.map { appCreator ->
            val app = appsRepository.read(appCreator.appId)
            get(userContext, appCreator, app)
        }
    }

    fun get(creatorContext: UserContext, appCreator: AppCreator, app: App): AppContext {
        val defaultAppRoles = defaultAppRolesRepository.findByApp(app)
        val appRoles = appRolesRepository.findByApp(app)
        val defaultRoles = appRolesRepository.findByAppDefaultRoles(defaultAppRoles)
        return AppContext(app, creatorContext.user, appCreator, defaultRoles, appRoles)
    }

    fun get(client: AppClient): AppContext {
        val app = appsRepository.read(client.id)
        val allRoles = appRolesRepository.findByApp(app)
        val defaultAppRoles = defaultAppRolesRepository.findByApp(app)
        val appRoles = appRolesRepository.findByAppDefaultRoles(defaultAppRoles)
        val appCreator = appCreatorsRepository.findByApp(app)!!
        val user = usersRepository.read(appCreator.userId)
        return AppContext(app, user, appCreator, appRoles, allRoles)
    }
}