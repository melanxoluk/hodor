package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.AppRole
import com.melanxoluk.hodor.domain.Application
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.LongCrudTable
import com.melanxoluk.hodor.server.storage.repositories.AppRolesRepository.AppRolesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.*


class AppRolesRepository: LongCrudRepository<AppRole, AppRolesTable>(AppRolesTable) {
    companion object AppRolesTable : LongCrudTable<AppRolesTable, AppRole>("app_roles") {

        private val _appId = reference("app_id", ApplicationsRepository)
        private val _uuid = uuid("uuid")
        private val _name = text("name")

        override val fieldsMapper: AppRole.(UpdateBuilder<Int>) -> Unit = {
            it[_appId] = EntityID(this.appId, ApplicationsRepository)
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

    fun findByAppAndName(app: Application, name: String) =
        findSingleBy { (_appId eq app.id) and (_name eq name) }
}
