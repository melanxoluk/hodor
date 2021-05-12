package ru.melanxoluk.hodor.domain.entities.repositories

import ru.melanxoluk.hodor.domain.entities.User
import ru.melanxoluk.hodor.domain.entities.UsernamePassword
import ru.melanxoluk.hodor.domain.LongCrudRepository
import ru.melanxoluk.hodor.domain.LongCrudTable
import ru.melanxoluk.hodor.domain.hodorPrefix
import ru.melanxoluk.hodor.domain.entities.repositories.AppsRepository.AppTable
import ru.melanxoluk.hodor.domain.entities.repositories.UsernamePasswordsRepository.*
import ru.melanxoluk.hodor.domain.entities.repositories.UsersRepository.UsersTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


class UsersRepository: LongCrudRepository<User, UsersTable>(UsersTable) {
    companion object UsersTable: LongCrudTable<UsersTable, User>("users") {

        private val _appId = reference("app_id", AppTable)
        private val _properties = text("properties")
        private val _uuid = uuid("uuid")

        override val fieldsMapper: User.(UpdateBuilder<Int>) -> Unit = {
            it[_appId] = EntityID(this.appId, AppTable)
            it[_properties] = this.properties
            it[_uuid] = this.uuid
        }

        override val table: UsersTable = this


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