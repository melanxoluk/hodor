package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.EmailPassword
import com.melanxoluk.hodor.server.storage.CrudTable
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.repositories.EmailPasswordsRepository.EmailPasswordTable
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


class EmailPasswordsRepository: LongCrudRepository<EmailPassword, EmailPasswordTable>(EmailPasswordTable) {
    companion object EmailPasswordTable: LongIdTable("email_passwords"),
                                     CrudTable<Long, EmailPasswordTable, EmailPassword> {

        private val _email = text("email")
        private val _password = text("password")

        override val fieldsMapper: EmailPassword.(UpdateBuilder<Int>) -> Unit = {
            it[_email] = this.email
            it[_password] = this.password
        }

        override val table: EmailPasswordTable = this


        override fun map(row: ResultRow) =
            EmailPassword(
                row[id].value,
                row[_email],
                row[_password])
    }


    fun findByEmail(email: String): EmailPassword? = with(table) {
        return transaction {
            return@transaction table
                .select { _email eq email }
                .singleOrNull()
                ?.let { map(it) }
        }
    }
}