package ru.melanxoluk.hodor.domain.entities

import ru.melanxoluk.hodor.domain.LongDomain
import java.util.*


data class AppRole(override val id: Long = 0L,
                   val uuid: UUID,
                   val appId: Long,
                   val name: String
                  ): LongDomain<AppRole> {

    val userRoles: List<UserRole>? = null
    val app: App? = null

    override fun inserted(id: Long) = copy(id = id)
}