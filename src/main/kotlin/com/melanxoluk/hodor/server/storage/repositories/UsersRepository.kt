package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.User
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.LongCrudTable
import com.melanxoluk.hodor.server.storage.hodorPrefix
import com.melanxoluk.hodor.server.storage.repositories.ApplicationsRepository.ApplicationsTable
import com.melanxoluk.hodor.server.storage.repositories.UsersRepository.UserTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.util.*


class UsersRepository: LongCrudRepository<User, UserTable>(UserTable) {
    companion object UserTable: LongCrudTable<UserTable, User>("users") {

        private val _applicationId = reference("application", ApplicationsTable)
        private val _properties = text("properties")
        private val _uuid = uuid("uuid")

        override val fieldsMapper: User.(UpdateBuilder<Int>) -> Unit = {
            it[_applicationId] = EntityID(this.appId, ApplicationsTable)
            it[_properties] = this.properties
            it[_uuid] = this.uuid
        }

        override val table: UserTable = this


        override fun map(row: ResultRow) =
            User(
                row[id].value,
                row[_applicationId].value,
                row[_properties])
    }


    fun findByUuid(uuid: UUID) = findSingleBy { _uuid eq uuid }

    fun findWithHodorPrefix() = findSingleBy { _properties eq hodorPrefix }
}
