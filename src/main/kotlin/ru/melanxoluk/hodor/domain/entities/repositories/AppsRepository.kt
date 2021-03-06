package ru.melanxoluk.hodor.domain.entities.repositories

import ru.melanxoluk.hodor.domain.LongCrudRepository
import ru.melanxoluk.hodor.domain.LongCrudTable
import ru.melanxoluk.hodor.domain.entities.App
import ru.melanxoluk.hodor.domain.entities.repositories.AppClientsRepository.AppClientsTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppsRepository.AppTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import ru.melanxoluk.hodor.common.notFoundResult
import java.util.*


class AppsRepository: LongCrudRepository<App, AppTable>(AppTable) {

    companion object AppTable: LongCrudTable<AppTable, App>("apps") {
        private val _uuid = uuid("uuid")
        private val _name = text("name").uniqueIndex()

        override val fieldsMapper: App.(UpdateBuilder<Int>) -> Unit = {
            it[_uuid] = this.uuid
            it[_name] = this.name
        }

        override val table = this

        override fun map(row: ResultRow) =
            App(row[id].value, row[_name], row[_uuid])
    }


    fun findByUuid(uuid: UUID) =
        find { _uuid eq uuid }

    fun findByName(name: String) =
        find { _name eq name }

    fun findByClientUuid(clientUuid: UUID): Result<App> {
        return notFoundResult(transaction {
            return@transaction table
                .leftJoin(AppClientsTable)
                .select { AppClientsTable._uuid eq clientUuid }
                .singleOrNull()
                ?.let { map(it) }
        })
    }
}
