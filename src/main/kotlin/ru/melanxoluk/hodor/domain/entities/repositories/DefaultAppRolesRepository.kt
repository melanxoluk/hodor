package ru.melanxoluk.hodor.domain.entities.repositories

import ru.melanxoluk.hodor.domain.LongCrudRepository
import ru.melanxoluk.hodor.domain.LongCrudTable
import ru.melanxoluk.hodor.domain.entities.App
import ru.melanxoluk.hodor.domain.entities.DefaultAppRole
import ru.melanxoluk.hodor.domain.entities.repositories.AppRolesRepository.AppRolesTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppsRepository.AppTable
import ru.melanxoluk.hodor.domain.entities.repositories.DefaultAppRolesRepository.DefaultAppRolesTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class DefaultAppRolesRepository: LongCrudRepository<DefaultAppRole, DefaultAppRolesTable>(DefaultAppRolesTable) {
    companion object DefaultAppRolesTable: LongCrudTable<DefaultAppRolesTable, DefaultAppRole>("default_app_roles") {

        private val _appId = reference("app_id", AppTable, ReferenceOption.CASCADE)
        private val _roleId = reference("role_id", AppRolesTable, ReferenceOption.CASCADE)

        override val fieldsMapper: DefaultAppRole.(UpdateBuilder<Int>) -> Unit = {
            it[_appId] = EntityID(this.appId, AppTable)
            it[_roleId] = EntityID(this.roleId, AppRolesTable)
        }

        override val table: DefaultAppRolesTable = this


        override fun map(row: ResultRow) =
            DefaultAppRole(
                row[id].value,
                row[_appId].value,
                row[_roleId].value)
    }


    fun findByApp(app: App) = findMany { _appId eq app.id }
}
