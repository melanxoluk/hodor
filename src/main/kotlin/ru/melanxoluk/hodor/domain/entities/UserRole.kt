package ru.melanxoluk.hodor.domain.entities

import ru.melanxoluk.hodor.domain.LongDomain


data class UserRole(override val id: Long = 0L,
                    val roleId: Long,
                    val userId: Long
                    ): LongDomain<UserRole> {

    override fun inserted(id: Long) = copy(id = id)
}