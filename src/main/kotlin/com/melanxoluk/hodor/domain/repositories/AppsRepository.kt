package com.melanxoluk.hodor.domain.repositories

import com.melanxoluk.hodor.domain.entities.App
import com.melanxoluk.hodor.domain.entities.User
import com.melanxoluk.hodor.domain.LongCrudRepository
import com.melanxoluk.hodor.domain.LongCrudTable
import com.melanxoluk.hodor.domain.repositories.AppClientsRepository.*
import com.melanxoluk.hodor.domain.repositories.AppsRepository.ApplicationsTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
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
