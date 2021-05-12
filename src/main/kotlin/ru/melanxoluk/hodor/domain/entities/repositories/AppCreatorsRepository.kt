package ru.melanxoluk.hodor.domain.entities.repositories

import ru.melanxoluk.hodor.domain.LongCrudRepository
import ru.melanxoluk.hodor.domain.LongCrudTable
import ru.melanxoluk.hodor.domain.entities.App
import ru.melanxoluk.hodor.domain.entities.AppCreator
import ru.melanxoluk.hodor.domain.entities.User
import ru.melanxoluk.hodor.domain.entities.repositories.AppCreatorsRepository.AppCreatorTable
import ru.melanxoluk.hodor.domain.entities.repositories.AppsRepository.AppTable
import ru.melanxoluk.hodor.domain.entities.repositories.UsersRepository.UsersTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder


// todo: write the reason of existing such table instead of fk on 'apps' table
class AppCreatorsRepository: LongCrudRepository<AppCreator, AppCreatorTable>(AppCreatorTable) {
    companion object AppCreatorTable: LongCrudTable<AppCreatorTable, AppCreator>("app_creators") {

        private val _appId = reference("app_id", AppTable, ReferenceOption.CASCADE)
        private val _userId = reference("user_id", UsersTable, ReferenceOption.CASCADE)

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
