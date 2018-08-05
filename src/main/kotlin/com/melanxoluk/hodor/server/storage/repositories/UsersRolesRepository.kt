package com.melanxoluk.hodor.server.storage.repositories

import com.melanxoluk.hodor.domain.UsersRole
import com.melanxoluk.hodor.server.storage.LongCrudRepository
import com.melanxoluk.hodor.server.storage.LongCrudTable
import com.melanxoluk.hodor.server.storage.repositories.AppRolesRepository.AppRolesTable
import com.melanxoluk.hodor.server.storage.repositories.UsersRepository.UserTable
import com.melanxoluk.hodor.server.storage.repositories.UsersRolesRepository.UsersRolesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class UsersRolesRepository: LongCrudRepository<UsersRole, UsersRolesTable>(UsersRolesTable) {
    companion object UsersRolesTable : LongCrudTable<UsersRolesTable, UsersRole>("users_roles") {

        private val _roleId = reference("role_id", AppRolesTable)
        private val _userId = reference("user_id", UserTable)

        override val fieldsMapper: UsersRole.(UpdateBuilder<Int>) -> Unit = {
            it[_roleId] = EntityID(this.userId, AppRolesTable)
            it[_userId] = EntityID(this.userId, UserTable)
        }

        override val table = this


        override fun map(row: ResultRow) =
            UsersRole(
                row[id].value,
                row[_roleId].value,
                row[_userId].value)
    }
}
