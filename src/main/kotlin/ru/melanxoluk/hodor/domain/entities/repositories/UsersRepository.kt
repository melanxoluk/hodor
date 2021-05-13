package ru.melanxoluk.hodor.domain.entities.repositories

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import ru.melanxoluk.hodor.common.notFoundResult
import ru.melanxoluk.hodor.domain.LongCrudRepository
import ru.melanxoluk.hodor.domain.LongCrudTable
import ru.melanxoluk.hodor.domain.entities.AppClient
import ru.melanxoluk.hodor.domain.entities.User
import ru.melanxoluk.hodor.domain.entities.repositories.AppsRepository.AppTable
import ru.melanxoluk.hodor.domain.entities.repositories.UsersRepository.UsersTable
import ru.melanxoluk.hodor.domain.hodorPrefix
import java.util.*


class UsersRepository: LongCrudRepository<User, UsersTable>(UsersTable) {
    companion object UsersTable: LongCrudTable<UsersTable, User>("users") {

        private val _appId = reference("app_id", AppTable, ReferenceOption.CASCADE)
        private val _username = varchar("username", 255)
        private val _password = varchar("password", 255)
        private val _properties = text("properties")
        private val _uuid = uuid("uuid")
        init {
            uniqueIndex(_appId, _username)
        }

        override val fieldsMapper: User.(UpdateBuilder<Int>) -> Unit = {
            it[_appId] = EntityID(this.appId, AppTable)
            it[_username] = this.username
            it[_password] = this.password
            it[_properties] = this.properties
            it[_uuid] = this.uuid
        }

        override val table: UsersTable = this


        override fun map(row: ResultRow) =
            User(
                row[id].value,
                row[_appId].value,
                row[_username],
                row[_password],
                row[_properties],
                row[_uuid])
    }


    fun findByUuid(uuid: UUID) =
        find { _uuid eq uuid }

    fun findWithHodorPrefix() =
        find { _properties eq hodorPrefix }

    fun findByAppAndEmail(appId: Long, username: String) =
        find { (_appId eq appId) and (_username eq username) }

    fun findByUsername(appId: Long, username: String) =
        find { (_appId eq appId) and (_username eq username) }

    fun isExistsUsername(client: AppClient, username: String): Boolean {
        return transaction {
            table.select { _username eq username }.count() > 0
        }
    }
}
