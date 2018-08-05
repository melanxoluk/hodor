package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.Application
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.LongCrudTable
import com.melanxoluk.hodor.server.storage.repositories.ApplicationsRepository.ApplicationsTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.*


class ApplicationsRepository: LongCrudRepository<Application, ApplicationsTable>(ApplicationsTable) {

    companion object ApplicationsTable: LongCrudTable<ApplicationsTable, Application>("applications") {
        private val _creatorId = reference("creator", UsersRepository.UserTable)
        private val _uuid = uuid("uuid")
        private val _name = text("name")

        override val fieldsMapper: Application.(UpdateBuilder<Int>) -> Unit = {
            it[_creatorId] = EntityID(this.creatorId, UsersRepository.UserTable)
            it[_uuid] = this.uuid
            it[_name] = this.name
        }

        override val table = this

        override fun map(row: ResultRow) =
            Application(
                row[id].value,
                row[_creatorId].value,
                row[_name],
                row[_uuid])
    }


    fun findByUuid(uuid: UUID) = findSingleBy { _uuid eq uuid }
}