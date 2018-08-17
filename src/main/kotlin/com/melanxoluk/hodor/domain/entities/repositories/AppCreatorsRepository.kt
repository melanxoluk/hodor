package com.melanxoluk.hodor.domain.entities.repositories

import com.melanxoluk.hodor.domain.LongCrudRepository
import com.melanxoluk.hodor.domain.LongCrudTable
import com.melanxoluk.hodor.domain.entities.App
import com.melanxoluk.hodor.domain.entities.AppCreator
import com.melanxoluk.hodor.domain.entities.User
import com.melanxoluk.hodor.domain.entities.repositories.AppCreatorsRepository.AppCreatorTable
import com.melanxoluk.hodor.domain.entities.repositories.AppsRepository.AppTable
import com.melanxoluk.hodor.domain.entities.repositories.UsersRepository.UsersTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder


class AppCreatorsRepository: LongCrudRepository<AppCreator, AppCreatorTable>(AppCreatorTable) {
    companion object AppCreatorTable: LongCrudTable<AppCreatorTable, AppCreator>("app_creators") {

        private val _appId = reference("app_id", AppTable)
        private val _userId = reference("user_id", UsersTable)

        override val fieldsMapper: AppCreator.(UpdateBuilder<Int>) -> Unit = {
            it[_appId] = EntityID(this.appId, AppTable)
            it[_userId] = EntityID(this.userId, UsersTable)
        }

        override val table: AppCreatorTable = this


        override fun map(row: ResultRow) =
            AppCreator(
                row[id].value,
                row[_appId].value,
                row[_userId].value)
    }


    fun findByApp(app: App) = findSingleBy { _appId eq app.id }

    fun findByUser(user: User) = findMany { _userId eq user.id }
}
