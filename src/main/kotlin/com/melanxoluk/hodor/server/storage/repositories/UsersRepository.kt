package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.User
import com.melanxoluk.hodor.server.storage.CrudTable
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.repositories.ApplicationsRepository.ApplicationsTable
import com.melanxoluk.hodor.server.storage.repositories.UsersRepository.UserTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class UsersRepository: LongCrudRepository<User, UserTable>(UserTable) {
    companion object UserTable: LongIdTable("users"),
                                     CrudTable<Long, UserTable, User> {

        // nullable necessary to create initial hodor entities
        private val _applicationId = reference("application", ApplicationsTable).nullable()
        private val _properties = text("properties")

        override val fieldsMapper: User.(UpdateBuilder<Int>) -> Unit = {
            it[_applicationId] = EntityID(this.applicationId, ApplicationsTable)
            it[_properties] = this.properties
        }

        override val table: UserTable = this


        override fun map(row: ResultRow) =
            User(
                row[id].value,
                row[_applicationId]?.value ?: 0L,
                row[_properties])
    }
}