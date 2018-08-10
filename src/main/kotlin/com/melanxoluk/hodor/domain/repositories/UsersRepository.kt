package com.melanxoluk.hodor.domain.repositories

import com.melanxoluk.hodor.domain.entities.User
import com.melanxoluk.hodor.domain.entities.UsernamePassword
import com.melanxoluk.hodor.domain.LongCrudRepository
import com.melanxoluk.hodor.domain.LongCrudTable
import com.melanxoluk.hodor.domain.hodorPrefix
import com.melanxoluk.hodor.domain.repositories.AppsRepository.ApplicationsTable
import com.melanxoluk.hodor.domain.repositories.UsernamePasswordsRepository.*
import com.melanxoluk.hodor.domain.repositories.UsersRepository.UserTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


class UsersRepository: LongCrudRepository<User, UserTable>(UserTable) {
    companion object UserTable: LongCrudTable<UserTable, User>("users") {

        private val _appId = reference("app_id", ApplicationsTable)
        private val _properties = text("properties")
        private val _uuid = uuid("uuid")

        override val fieldsMapper: User.(UpdateBuilder<Int>) -> Unit = {
            it[_appId] = EntityID(this.appId, ApplicationsTable)
            it[_properties] = this.properties
            it[_uuid] = this.uuid
        }

        override val table: UserTable = this


        override fun map(row: ResultRow) =
            User(
                row[id].value,
                row[_appId].value,
                row[_properties],
                row[_uuid])
    }


    fun findByUuid(uuid: UUID) = findSingleBy { _uuid eq uuid }

    fun findWithHodorPrefix() = findSingleBy { _properties eq hodorPrefix }

    fun findByAppAndEmail(appId: Long, username: String): Pair<UsernamePassword, User>? {
        return transaction {
            return@transaction table
                .leftJoin(UsernamePasswordTable)
                .select { (_appId eq appId) and (UsernamePasswordTable._username eq username) }
                .singleOrNull()
                ?.let { UsernamePasswordTable.map(it) to map(it) }
        }
    }
}
