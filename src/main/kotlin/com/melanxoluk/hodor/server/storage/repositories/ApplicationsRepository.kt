package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.Application
import com.melanxoluk.hodor.server.storage.CrudTable
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.repositories.ApplicationUsersRepository.UsersTable
import com.melanxoluk.hodor.server.storage.repositories.ApplicationsRepository.ApplicationsTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.*


class ApplicationsRepository
    : LongCrudRepository<
        Application,
        ApplicationsTable>(
            ApplicationsTable) {

    companion object ApplicationsTable: LongIdTable("applications"),
                                        CrudTable<Long, ApplicationsTable, Application> {

        private val _creatorId = reference("creator", UsersTable)
        private val _uuid = uuid("uuid")
        private val _name = text("name")

        override val fieldsMapper: Application.(UpdateBuilder<Int>) -> Unit = {
            it[_creatorId] = EntityID(this.creatorId, UsersTable)
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