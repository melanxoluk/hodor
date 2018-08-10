package com.melanxoluk.hodor.services

import com.melanxoluk.hodor.common.UsernameLogin
import org.koin.standalone.get


class ContextService : Service {
    private val usersService = get<UsersService>()

    fun getOrCreate(login: UsernameLogin) {
        // client -> app <- user <- usernamePassword
    }
}