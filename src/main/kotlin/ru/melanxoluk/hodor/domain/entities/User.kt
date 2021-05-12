package ru.melanxoluk.hodor.domain.entities

import ru.melanxoluk.hodor.domain.LongDomain
import java.util.*


data class User(override val id: Long = 0L,
                val appId: Long,
                val properties: String,
                val uuid: UUID
               ) : LongDomain<User> {

    var roles: List<AppRole>? = null
    var app: App? = null

    override fun inserted(id: Long) = copy(id = id)
}