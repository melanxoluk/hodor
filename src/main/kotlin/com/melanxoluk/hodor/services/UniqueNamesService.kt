package com.melanxoluk.hodor.services

import com.melanxoluk.hodor.domain.entities.AppClient


class UniqueNamesService: Service() {
    fun isUniqueUsername(client: AppClient, username: String): Boolean {
        // client -> app <- users <- usernames
        return usernamePasswordRepository.isExistsUsername(client, username)
    }
}