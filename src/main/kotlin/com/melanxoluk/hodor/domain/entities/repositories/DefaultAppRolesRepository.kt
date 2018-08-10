package com.melanxoluk.hodor.domain.entities.repositories

import com.melanxoluk.hodor.domain.LongCrudRepository
import com.melanxoluk.hodor.domain.LongCrudTable
import com.melanxoluk.hodor.domain.entities.App
import com.melanxoluk.hodor.domain.entities.DefaultAppRole
import com.melanxoluk.hodor.domain.entities.repositories.AppRolesRepository.AppRolesTable
import com.melanxoluk.hodor.domain.entities.repositories.AppsRepository.ApplicationsTable
import com.melanxoluk.hodor.domain.entities.repositories.DefaultAppRolesRepository.DefaultAppRolesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class DefaultAppRolesRepository: LongCrudRepository<DefaultAppRole, DefaultAppRolesTable>(DefaultAppRolesTable) {
    companion object DefaultAppRolesTable: LongCrudTable<DefaultAppRolesTable, DefaultAppRole>("default_app_roles") {

        private val _appId = reference("app_id", ApplicationsTable)
        private val _roleId = reference("role_id", AppRolesTable)

        override val fieldsMapper: DefaultAppRole.(UpdateBuilder<Int>) -> Unit = {
            it[_appId] = EntityID(this.appId, ApplicationsTable)
            it[_roleId] = EntityID(this.roleId, AppRolesTable)
        }

        override val table: DefaultAppRolesTable = this


        override fun map(row: ResultRow) =
            DefaultAppRole(
                row[id].value,
                row[_appId].value,
                row[_roleId].value)
    }


    fun findByApp(app: App) = findSingleBy { _appId eq app.id }
}
