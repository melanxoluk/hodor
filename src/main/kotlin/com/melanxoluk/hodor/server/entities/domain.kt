package com.melanxoluk.hodor.server.entities

import com.melanxoluk.hodor.domain.LongDomain


enum class UserType {
    HODOR, APP
}

data class AuthenticationEntry(override val id: Long = 0L,
                               var userType: UserType = UserType.APP,
                               var userId: Long = 0L,
                               var token: String = ""
                              ): LongDomain<AuthenticationEntry> {

    override fun inserted(id: Long) = copy(id = id)
}