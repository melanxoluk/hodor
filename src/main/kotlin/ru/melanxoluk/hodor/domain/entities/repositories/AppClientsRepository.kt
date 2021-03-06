package ru.melanxoluk.hodor.domain.entities.repositories

import ru.melanxoluk.hodor.domain.entities.AppClient
import ru.melanxoluk.hodor.domain.entities.App
import ru.melanxoluk.hodor.domain.LongCrudRepository
import ru.melanxoluk.hodor.domain.LongCrudTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppsRepository.AppTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppClientsRepository.AppClientsTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import ru.melanxoluk.hodor.common.notFoundResult
import ru.melanxoluk.hodor.common.result
import java.util.*


class AppClientsRepository: LongCrudRepository<AppClient, AppClientsTable>(AppClientsTable) {
    companion object AppClientsTable: LongCrudTable<AppClientsTable, AppClient>("app_clients") {

        val _appId = reference("app_id", AppTable, ReferenceOption.CASCADE)
        val _type = text("type")
        val _uuid = uuid("uuid")

        override val fieldsMapper: AppClient.(UpdateBuilder<Int>) -> Unit = {
            it[_appId] = EntityID(this.appId, AppTable)
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


    fun findByUuid(uuid: UUID) =
        find { _uuid eq uuid }

    fun findByApp(app: App) =
        findMany { _appId eq app.id }

    fun findByAppAndType(app: App, type: String) =
        find { (_appId eq app.id) and (_type eq type) }
}
