package ru.melanxoluk.hodor.domain.entities.repositories

import ru.melanxoluk.hodor.domain.entities.AppRole
import ru.melanxoluk.hodor.domain.entities.User
import ru.melanxoluk.hodor.domain.entities.UserRole
import ru.melanxoluk.hodor.domain.LongCrudRepository
import ru.melanxoluk.hodor.domain.LongCrudTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppRolesRepository.AppRolesTable
import ru.melanxoluk.hodor.domain.entities.repositories.UsersRepository.UsersTable
import ru.melanxoluk.hodor.domain.entities.repositories.UserRolesRepository.UserRolesTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class UserRolesRepository: LongCrudRepository<UserRole, UserRolesTable>(UserRolesTable) {
    companion object UserRolesTable : LongCrudTable<UserRolesTable, UserRole>("user_roles") {

        private val _roleId = reference("role_id", AppRolesTable, ReferenceOption.CASCADE)
        private val _userId = reference("user_id", UsersTable, ReferenceOption.CASCADE)

        override val fieldsMapper: UserRole.(UpdateBuilder<Int>) -> Unit = {
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
