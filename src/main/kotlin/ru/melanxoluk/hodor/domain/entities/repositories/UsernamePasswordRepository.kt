package ru.melanxoluk.hodor.domain.entities.repositories

import ru.melanxoluk.hodor.domain.entities.User
import ru.melanxoluk.hodor.domain.entities.UsernamePassword
import ru.melanxoluk.hodor.domain.CrudTable
import ru.melanxoluk.hodor.domain.LongCrudRepository
import ru.melanxoluk.hodor.domain.entities.AppClient
import ru.melanxoluk.hodor.domain.entities.repositories.AppsRepository.*
import ru.melanxoluk.hodor.domain.entities.repositories.UsernamePasswordsRepository.UsernamePasswordTable
import ru.melanxoluk.hodor.domain.entities.repositories.UsersRepository.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import ru.melanxoluk.hodor.common.notFoundResult


class UsernamePasswordsRepository: LongCrudRepository<UsernamePassword, UsernamePasswordTable>(UsernamePasswordTable) {
    companion object UsernamePasswordTable: LongIdTable("username_passwords"),
        CrudTable<Long, UsernamePasswordTable, UsernamePassword> {

        val _username = text("username")
        val _password = text("password")
        val _userId = reference("user_id", UsersRepository)

        override val fieldsMapper: UsernamePassword.(UpdateBuilder<Int>) -> Unit = {
            it[_username] = this.username
            it[_password] = this.password
            it[_userId] = EntityID(this.userId, UsersRepository)
        }

        override val table: UsernamePasswordTable = this


        override fun map(row: ResultRow) =
            UsernamePassword(
                row[id].value,
                row[_username],
                row[_password],
                row[_userId].value)
    }


    fun findByUsername(username: String): Result<UsernamePassword> = with(table) {
        return notFoundResult(transaction {
            return@transaction table
                .select { _username eq username }
                .singleOrNull()
                ?.let { map(it) }
        })
    }

    fun findByUser(user: User) = findSingleBy { _userId eq user.id }

    fun isExistsUsername(client: AppClient, username: String): Boolean {
        var isExists = false
        transaction {
            // client -> app <- users <- usernames
            val rows = table
                .leftJoin(AppTable.leftJoin(UsersTable.leftJoin(UsernamePasswordTable)))
                .select { UsernamePasswordTable._username eq username }

            isExists = rows.count() > 0
        }
        return isExists
    }
}
