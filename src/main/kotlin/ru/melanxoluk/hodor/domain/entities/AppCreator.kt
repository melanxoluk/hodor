package ru.melanxoluk.hodor.domain.entities

import ru.melanxoluk.hodor.domain.LongDomain


data class AppCreator(
    override val id: Long = 0,
    val appId: Long,
    val userId: Long)
    : LongDomain<AppCreator> {

    override fun inserted(id: Long) = copy(id = id)
}
