package com.melanxoluk.hodor.server.services

import com.melanxoluk.hodor.domain.HodorUser
import com.melanxoluk.hodor.domain.HodorUserType
import com.melanxoluk.hodor.server.storage.repositories.HodorUsersRepository
import org.koin.standalone.inject


data class LoginedHodorUser(val hodorUserType: HodorUserType,
                            val token: String)

class HodorUsersService: Service {
    private val authService by inject<AuthService>()
    private val repository by inject<HodorUsersRepository>()

    // necessary to notify about type of logined user, to determine
    // which type of app should be run: admin or usual
    /*fun login(email: String, password: String): ServiceResult<LoginedHodorUser> {
        return authService.loginHodorUser(email, password)
    }

    fun createAdmin(email: String, password: String) = ok {
        *//*repository.create(HodorUser(
            userType = HodorUserType.ADMIN,
            password = password,
            email = email))*//*
    }

    fun createUser(email: String, password: String) = ok {
        repository.create(HodorUser(
            userType = HodorUserType.REGULAR,
            password = password,
            email = email))
    }

    fun getUser(token: String) =
        authService.getHodorUser(token)

    fun removeUser(id: Long) = ok {
        repository.delete(id)
    }*/
}
