package com.melanxoluk.hodor.domain.entities.repositories

import com.melanxoluk.hodor.domain.entities.AppRole
import com.melanxoluk.hodor.domain.entities.App
import com.melanxoluk.hodor.domain.LongCrudRepository
import com.melanxoluk.hodor.domain.LongCrudTable
import com.melanxoluk.hodor.domain.entities.DefaultAppRole
import com.melanxoluk.hodor.domain.entities.UserRole
import com.melanxoluk.hodor.domain.entities.repositories.AppRolesRepository.AppRolesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.*


class AppRolesRepository: LongCrudRepository<AppRole, AppRolesTable>(AppRolesTable) {
    companion object AppRolesTable : LongCrudTable<AppRolesTable, AppRole>("app_roles") {

        private val _appId = reference("app_id", AppsRepository)
        private val _uuid = uuid("uuid")
        private val _name = text("name")

        override val fieldsMapper: AppRole.(UpdateBuilder<Int>) -> Unit = {
            it[_appId] = EntityID(this.appId, AppsRepository)
            it[_uuid] = this.uuid
            it[_name] = this.name
        }

        override val table: AppRolesTable = this


        override fun map(row: ResultRow) =
            AppRole(
                row[id].value,
                row[_uuid],
                row[_appId].value,
                row[_name])
    }


    fun findByUuid(uuid: UUID) = findSingleBy { _uuid eq uuid }

    fun findByApp(app: App) =
        findMany { _appId eq app.id }

    fun findByAppAndName(app: App, name: String) =
        findSingleBy { (_appId eq app.id) and (_name eq name) }

    fun findByUserRoles(roles: List<UserRole>) = with(roles.map { it.roleId }) {
        findMany { id inList this@with }
    }

    fun findByAppDefaultRoles(roles: List<DefaultAppRole>) = with(roles.map { it.roleId }) {
        findMany { id inList this@with }
    }
}
