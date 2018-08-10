package com.melanxoluk.hodor.domain.entities.repositories

import com.melanxoluk.hodor.domain.LongCrudRepository
import com.melanxoluk.hodor.domain.LongCrudTable
import com.melanxoluk.hodor.domain.entities.App
import com.melanxoluk.hodor.domain.entities.repositories.AppClientsRepository.AppClientsTable
import com.melanxoluk.hodor.domain.entities.repositories.AppsRepository.ApplicationsTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


class AppsRepository: LongCrudRepository<App, ApplicationsTable>(ApplicationsTable) {

    companion object ApplicationsTable: LongCrudTable<ApplicationsTable, App>("apps") {
        private val _uuid = uuid("uuid")
        private val _name = text("name")

        override val fieldsMapper: App.(UpdateBuilder<Int>) -> Unit = {
            it[_uuid] = this.uuid
            it[_name] = this.name
        }

        override val table = this

        override fun map(row: ResultRow) =
            App(
                row[id].value,
                row[_name],
                row[_uuid])
    }


    fun findByUuid(uuid: UUID) = findSingleBy { _uuid eq uuid }

    fun findByName(name: String) = findSingleBy { _name eq name }

    fun findByClientUuid(clientUuid: UUID): App? {
        return transaction {
            return@transaction table
                .leftJoin(AppClientsTable)
                .select { AppClientsTable._uuid eq clientUuid }
                .singleOrNull()
                ?.let { map(it) }
        }
    }
}
