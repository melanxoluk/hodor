package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.UsernamePassword
import com.melanxoluk.hodor.server.storage.CrudTable
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.repositories.UsernamePasswordsRepository.UsernamePasswordTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


class UsernamePasswordsRepository: LongCrudRepository<UsernamePassword, UsernamePasswordTable>(UsernamePasswordTable) {
    companion object UsernamePasswordTable: LongIdTable("username_passwords"),
        CrudTable<Long, UsernamePasswordTable, UsernamePassword> {

        val _username = text("username")
        val _password = text("password")
        val _user = reference("user", UsersRepository.UserTable)

        override val fieldsMapper: UsernamePassword.(UpdateBuilder<Int>) -> Unit = {
            it[_username] = this.username
            it[_password] = this.password
            it[_user] = EntityID(this.userId, UsersRepository.UserTable)
        }

        override val table: UsernamePasswordTable = this


        override fun map(row: ResultRow) =
            UsernamePassword(
                row[id].value,
                row[_username],
                row[_password],
                row[_user].value)
    }


    fun findByUsername(username: String): UsernamePassword? = with(table) {
        return transaction {
            return@transaction table
                .select { _username eq username }
                .singleOrNull()
                ?.let { map(it) }
        }
    }
}
