package ru.melanxoluk.hodor.domain.entities.repositories

import ru.melanxoluk.hodor.domain.entities.EmailPassword
import ru.melanxoluk.hodor.domain.CrudTable
import ru.melanxoluk.hodor.domain.LongCrudRepository
import ru.melanxoluk.hodor.domain.entities.repositories.EmailPasswordsRepository.EmailPasswordTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


class EmailPasswordsRepository: LongCrudRepository<EmailPassword, EmailPasswordTable>(EmailPasswordTable) {
    companion object EmailPasswordTable: LongIdTable("email_passwords"),
        CrudTable<Long, EmailPasswordTable, EmailPassword> {

        val _hodorUser = reference("hodor_user", HodorUsersRepository)
        val _password = text("password")
        val _email = text("email")

        override val fieldsMapper: EmailPassword.(UpdateBuilder<Int>) -> Unit = {
            it[_hodorUser] = EntityID(this.userId, HodorUsersRepository)
            it[_password] = this.password
            it[_email] = this.email
        }

        override val table: EmailPasswordTable = this


        override fun map(row: ResultRow) =
            EmailPassword(
                row[id].value,
                row[_email],
                row[_password],
                row[_hodorUser].value)
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
