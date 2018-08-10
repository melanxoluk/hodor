package com.melanxoluk.hodor.domain.repositories

import com.melanxoluk.hodor.domain.EmailPassword
import com.melanxoluk.hodor.domain.HodorUser
import com.melanxoluk.hodor.domain.HodorUserType
import com.melanxoluk.hodor.domain.CrudTable
import com.melanxoluk.hodor.domain.LongCrudRepository
import com.melanxoluk.hodor.domain.repositories.HodorUsersRepository.HodorUserTable
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


class HodorUsersRepository: LongCrudRepository<HodorUser, HodorUserTable>(HodorUserTable) {
    companion object HodorUserTable: LongIdTable("hodor_users"),
        CrudTable<Long, HodorUserTable, HodorUser> {

        private val _userType = enumeration("user_type", HodorUserType::class.java)

        override val fieldsMapper: HodorUser.(UpdateBuilder<Int>) -> Unit = {
            it[_userType] = this.userType
        }

        override val table: HodorUserTable = this


        override fun map(row: ResultRow) =
            HodorUser(
                row[id].value,
                row[_userType])
    }


    // holy fucking shit
    fun findByEmail(email: String): HodorUser? = with(table) {
        return transaction {
            return@transaction table
                .leftJoin(EmailPasswordsRepository.EmailPasswordTable)
                .select { EmailPasswordsRepository.EmailPasswordTable._email eq email }
                .singleOrNull()
                ?.let { map(it).apply {
                    val emailPass = EmailPasswordsRepository.EmailPasswordTable.map(it)
                    if (this.emailPasswords == null) {
                        this.emailPasswords = mutableListOf()
                    }

                    (this.emailPasswords as MutableList<EmailPassword>).add(emailPass)
                } }
        }
    }
}