package com.melanxoluk.hodor.services

import com.melanxoluk.hodor.common.SimpleUsernameLogin
import org.koin.standalone.get


class ContextService : Service {
    private val usersService = get<UsersService>()

    fun getOrCreate(login: SimpleUsernameLogin) {
        // client -> app <- user <- usernamePassword
    }
}