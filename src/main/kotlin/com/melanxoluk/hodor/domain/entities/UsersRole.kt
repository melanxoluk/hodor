package com.melanxoluk.hodor.domain.entities

import com.melanxoluk.hodor.domain.LongDomain


data class UsersRole(override val id: Long = 0L,
                     val roleId: Long,
                     val userId: Long
                    ): LongDomain<UsersRole> {

    override fun inserted(id: Long) = copy(id = id)
}