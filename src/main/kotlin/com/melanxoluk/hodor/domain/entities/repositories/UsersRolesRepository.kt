package com.melanxoluk.hodor.domain.entities.repositories

import com.melanxoluk.hodor.domain.entities.AppRole
import com.melanxoluk.hodor.domain.entities.User
import com.melanxoluk.hodor.domain.entities.UserRole
import com.melanxoluk.hodor.domain.LongCrudRepository
import com.melanxoluk.hodor.domain.LongCrudTable
import com.melanxoluk.hodor.domain.entities.repositories.AppRolesRepository.AppRolesTable
import com.melanxoluk.hodor.domain.entities.repositories.UsersRepository.UsersTable
import com.melanxoluk.hodor.domain.entities.repositories.UsersRolesRepository.UsersRolesTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class UsersRolesRepository: LongCrudRepository<UserRole, UsersRolesTable>(UsersRolesTable) {
    companion object UsersRolesTable : LongCrudTable<UsersRolesTable, UserRole>("users_roles") {

        private val _roleId = reference("role_id", AppRolesTable)
        private val _userId = reference("user_id", UsersTable)

        val FIELDS_MAPPER: UserRole.(UpdateBuilder<Int>) -> Unit = {
            it[_roleId] = EntityID(this.roleId, AppRolesTable)
            it[_userId] = EntityID(this.userId, UsersTable)
        }

        override val table = this


        override fun map(row: ResultRow) =
            UserRole(
                row[id].value,
                row[_roleId].value,
                row[_userId].value)
    }


    fun findByUserAndRole(user: User, role: AppRole) =
        findSingleBy { (_roleId eq role.id) and (_userId eq user.id) }

    fun findByUser(user: User) = findMany { _userId eq user.id }
}