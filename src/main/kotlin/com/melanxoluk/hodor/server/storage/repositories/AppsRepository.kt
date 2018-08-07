package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.App
import com.melanxoluk.hodor.domain.User
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.LongCrudTable
import com.melanxoluk.hodor.server.storage.repositories.AppsRepository.ApplicationsTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.*


class AppsRepository: LongCrudRepository<App, ApplicationsTable>(ApplicationsTable) {

    companion object ApplicationsTable: LongCrudTable<ApplicationsTable, App>("apps") {
        private val _creatorId = reference("creator_id", UsersRepository.UserTable)
        private val _uuid = uuid("uuid")
        private val _name = text("name")

        override val fieldsMapper: App.(UpdateBuilder<Int>) -> Unit = {
            it[_creatorId] = EntityID(this.creatorId, UsersRepository.UserTable)
            it[_uuid] = this.uuid
            it[_name] = this.name
        }

        override val table = this

        override fun map(row: ResultRow) =
            App(
                row[id].value,
                row[_creatorId].value,
                row[_name],
                row[_uuid])
    }


    fun findByUuid(uuid: UUID) = findSingleBy { _uuid eq uuid }

    fun findByCreator(creator: User) = findSingleBy { _creatorId eq creator.id }
}
