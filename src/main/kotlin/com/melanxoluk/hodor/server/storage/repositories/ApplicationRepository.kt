package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.Application
import com.melanxoluk.hodor.server.storage.CrudTable
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.repositories.ApplicationRepository.ApplicationsTable
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class ApplicationRepository
    : LongCrudRepository<
        Application,
        ApplicationsTable>(
            ApplicationsTable) {

    companion object ApplicationsTable: LongIdTable("applications"),
                                        CrudTable<Long, ApplicationsTable, Application> {
        private val _creatorId = long("creator")
        private val _token = text("token")
        private val _name = text("name")

        override val fieldsMapper: Application.(UpdateBuilder<Int>) -> Unit = {
            it[_creatorId] = this.creatorId
            it[_token] = this.token
            it[_name] = this.name
        }

        override val table = this

        override fun map(row: ResultRow) = Application(
            row[id].value,
            row[_creatorId],
            row[_token],
            row[_name])
    }
}