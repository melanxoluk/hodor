package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.ApplicationUser
import com.melanxoluk.hodor.server.storage.CrudTable
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.repositories.ApplicationUsersRepository.UsersTable
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


class ApplicationUsersRepository
    : LongCrudRepository<
        ApplicationUser,
        UsersTable>(
            UsersTable) {

    companion object UsersTable: LongIdTable("users"),
                                 CrudTable<Long, UsersTable, ApplicationUser> {

        private val _application = long("application_id")
        private val _properties = text("properties")
        private val _password = text("password")
        private val _email = text("email")

        override val fieldsMapper: ApplicationUser.(UpdateBuilder<Int>) -> Unit = {
            it[_application] = this.applicationId
            it[_properties] = this.properties
            it[_password] = this.password
            it[_email] = this.email
        }

        override val table = UsersTable

        override fun map(row: ResultRow) = ApplicationUser(
            row[id].value,
            row[_application],
            row[_email],
            row[_password],
            row[_properties])
    }


    fun findByEmail(email: String) = findSingleBy { _email eq email }

    fun findUser(appId: Long, email: String) = findSingleBy {
        (_application eq appId) and (_email eq email)
    }
}