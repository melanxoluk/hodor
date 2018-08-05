package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.AppClient
import com.melanxoluk.hodor.domain.Application
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.LongCrudTable
import com.melanxoluk.hodor.server.storage.repositories.ApplicationsRepository.ApplicationsTable
import com.melanxoluk.hodor.server.storage.repositories.AppClientsRepository.AppClientsTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.*


class AppClientsRepository: LongCrudRepository<AppClient, AppClientsTable>(AppClientsTable) {
    companion object AppClientsTable: LongCrudTable<AppClientsTable, AppClient>("app_clients") {

        private val _appId = reference("app_id", ApplicationsTable)
        private val _type = text("type")
        private val _uuid = uuid("uuid")

        override val fieldsMapper: AppClient.(UpdateBuilder<Int>) -> Unit = {
            it[_appId] = EntityID(this.appId, ApplicationsTable)
            it[_type] = this.type
            it[_uuid] = this.uuid
        }

        override val table: AppClientsTable = this


        override fun map(row: ResultRow) =
            AppClient(
                row[id].value,
                row[_appId].value,
                row[_type],
                row[_uuid])
    }


    fun findByUuid(uuid: UUID) = findSingleBy { _uuid eq uuid }

    fun findByApp(app: Application) = findSingleBy { _appId eq app.id }
}