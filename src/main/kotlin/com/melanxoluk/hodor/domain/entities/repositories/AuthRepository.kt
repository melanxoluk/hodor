package com.melanxoluk.hodor.domain.entities.repositories

import com.melanxoluk.hodor.domain.entities.AuthenticationEntry
import com.melanxoluk.hodor.domain.entities.UserType
import com.melanxoluk.hodor.domain.CrudTable
import com.melanxoluk.hodor.domain.LongCrudRepository
import com.melanxoluk.hodor.domain.entities.repositories.AuthRepository.AuthenticationEntriesTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class AuthRepository
    : LongCrudRepository<
    AuthenticationEntry,
    AuthenticationEntriesTable>(
            AuthenticationEntriesTable) {

    companion object AuthenticationEntriesTable
        : LongIdTable("auth_tokens"),
        CrudTable<
            Long,
            AuthenticationEntriesTable,
            AuthenticationEntry> {

        private val _userType = enumeration("user_type", UserType::class)
        private val _userId = long("user_id")
        private val _token = text("token")

        override val fieldsMapper: AuthenticationEntry.(UpdateBuilder<Int>) -> Unit = {
            it[_userType] = this.userType
            it[_userId] = this.userId
            it[_token] = this.token
        }

        override val table = this

        override fun map(row: ResultRow) =
            AuthenticationEntry(
                row[id].value,
                row[_userType],
                row[_userId],
                row[_token])
    }




    fun findByToken(token: String) = findSingleBy { _token eq token }

    fun findByHodorUser(userId: Long) = findSingleBy {
        (_userId eq userId) and  (_userType eq UserType.HODOR)
    }

    fun findByAppUser(userId: Long) = findSingleBy {
        (_userId eq userId) and  (_userType eq UserType.APP)
    }
}