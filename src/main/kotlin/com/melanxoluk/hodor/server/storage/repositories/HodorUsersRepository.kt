package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.HodorUser
import com.melanxoluk.hodor.domain.HodorUserType
import com.melanxoluk.hodor.server.storage.CrudTable
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.repositories.HodorUsersRepository.HodorUserTable
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


class HodorUsersRepository: LongCrudRepository<HodorUser, HodorUserTable>(HodorUserTable) {
    companion object HodorUserTable: LongIdTable("hodor_users"),
                                     CrudTable<Long, HodorUserTable, HodorUser> {

        private val _userType = enumeration("user_type", HodorUserType::class.java)
        private val _password = text("password")
        private val _email = text("email")

        override val fieldsMapper: HodorUser.(UpdateBuilder<Int>) -> Unit = {
            it[_userType] = this.userType
            it[_password] = this.password
            it[_email] = this.email
        }

        override val table: HodorUserTable = this


        override fun map(row: ResultRow) =
            HodorUser(
                row[id].value,
                row[_userType],
                row[_password],
                row[_email])
    }


    fun findByEmail(email: String): HodorUser? = with(table) {
        return transaction {
            return@transaction table
                .select { _email eq email }
                .singleOrNull()
                ?.let { map(it) }
        }
    }
}