package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.EmailPasswordAuthentication
import com.melanxoluk.hodor.server.storage.CrudTable
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.repositories.EmailPasswordsAuthRepository.EmailPasswordsAuthenticationTable
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class EmailPasswordsAuthRepository
    : LongCrudRepository<
    EmailPasswordAuthentication,
    EmailPasswordsAuthenticationTable>(
    EmailPasswordsAuthenticationTable) {

    companion object EmailPasswordsAuthenticationTable
        : LongIdTable("email_password_tokens"),
          CrudTable<
              Long,
              EmailPasswordsAuthenticationTable,
              EmailPasswordAuthentication> {

        private val _emailPasswordId = long("email_password_id")
        private val _token = text("token")

        override val fieldsMapper: EmailPasswordAuthentication.(UpdateBuilder<Int>) -> Unit = {
            it[_emailPasswordId] = this.emailPasswordId
            it[_token] = this.token
        }

        override val table = this

        override fun map(row: ResultRow) =
            EmailPasswordAuthentication(
                row[id].value,
                row[_emailPasswordId],
                row[_token])
    }


    fun findByToken(token: String) = findSingleBy { _token eq token }

    fun findByEmailPasswordId(emailPassId: Long) = findSingleBy { _emailPasswordId eq emailPassId }
}