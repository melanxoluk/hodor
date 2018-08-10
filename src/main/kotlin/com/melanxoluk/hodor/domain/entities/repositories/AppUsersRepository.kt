package com.melanxoluk.hodor.domain.entities.repositories

import com.melanxoluk.hodor.domain.entities.AppUser
import com.melanxoluk.hodor.domain.CrudTable
import com.melanxoluk.hodor.domain.LongCrudRepository
import com.melanxoluk.hodor.domain.entities.repositories.AppUsersRepository.AppUsersTable
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class AppUsersRepository: LongCrudRepository<AppUser, AppUsersTable>(AppUsersTable) {

    companion object AppUsersTable: LongIdTable("app_users"),
        CrudTable<Long, AppUsersTable, AppUser> {

        private val _application = long("application_id")
        private val _properties = text("properties")
        private val _password = text("password")
        private val _email = text("email")

        override val fieldsMapper: AppUser.(UpdateBuilder<Int>) -> Unit = {
            it[_application] = this.applicationId
            it[_properties] = this.properties
            it[_password] = this.password
            it[_email] = this.email
        }

        override val table = AppUsersTable

        override fun map(row: ResultRow) = AppUser(
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