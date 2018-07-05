package com.melanxoluk.hodor.server.services

import com.melanxoluk.hodor.domain.ApplicationUser
import com.melanxoluk.hodor.domain.HodorUser
import com.melanxoluk.hodor.secure.PasswordHasher
import com.melanxoluk.hodor.secure.TokenGenerator
import com.melanxoluk.hodor.server.entities.AuthenticationEntry
import com.melanxoluk.hodor.server.entities.LoginedHodorUser
import com.melanxoluk.hodor.server.entities.UserType
import com.melanxoluk.hodor.server.storage.repositories.AuthRepository
import com.melanxoluk.hodor.server.storage.repositories.HodorUsersRepository
import com.melanxoluk.hodor.server.storage.repositories.ApplicationUsersRepository
import org.koin.standalone.inject


class AuthService: Service {
    companion object {
        private val NOT_FOUND_EMAIL = "User with such email not registered"
        private val WRONG_PASSWORD = "Wrong password"
        private val NOT_AUTH = "Not authenticated"
    }

    private val hodorUsersRepository by inject<HodorUsersRepository>()
    private val appUsersRepository by inject<ApplicationUsersRepository>()
    private val passwordHasher by inject<PasswordHasher>()
    private val authRepository by inject<AuthRepository>()
    private val tokenGenerator by inject<TokenGenerator>()


    // ~~~ hodor users flow

    fun loginHodorUser(email: String,
                       password: String): ServiceResult<LoginedHodorUser> {

        // email check
        val hodorUser = hodorUsersRepository.findByEmail(email)
            ?: return clientError(NOT_FOUND_EMAIL)

        // pass hash check
        val consumedHash = passwordHasher.hash(password)
        val storedHash = hodorUser.password
        if (consumedHash != storedHash) {
            return clientError(WRONG_PASSWORD)
        }

        // checks are processed, return token
        return ok {
            val token = refreshToken(hodorUser.id, UserType.HODOR)
            LoginedHodorUser(hodorUser.userType, token)
        }
    }

    fun getHodorUser(token: String): ServiceResult<HodorUser> {
        val authEntry = authRepository.findByToken(token)
            ?: return clientError(NOT_AUTH)

        return ok { hodorUsersRepository.read(authEntry.userId) }
    }


    // ~~~ app users flow

    fun loginApplicationUser(appId: Long,
                             email: String,
                             password: String): ServiceResult<String> {

        // email check
        val appUser = appUsersRepository.findUser(appId, email)
            ?: return clientError(NOT_FOUND_EMAIL)

        // pass hash check
        val consumedHash = passwordHasher.hash(password)
        val storedHash = appUser.password
        if (consumedHash != storedHash) {
            return clientError(WRONG_PASSWORD)
        }

        // check are processed, return token
        return ok { refreshToken(appUser.id, UserType.APP) }
    }

    fun getAppUser(token: String): ServiceResult<ApplicationUser> {
        val authEntry = authRepository.findByToken(token)
            ?: return clientError(NOT_AUTH)

        return ok { appUsersRepository.read(authEntry.userId) }
    }


    // ~~~ misc

    private fun refreshToken(userId: Long, userType: UserType): String {
        // we need new token for user
        val newToken = tokenGenerator.generate()

        // and check previous entry
        val existedEntry = if (userType == UserType.HODOR) {
            authRepository.findByHodorUser(userId)
        } else {
            authRepository.findByAppUser(userId)
        }

        // create new if not found
        if (existedEntry == null) {
            authRepository
                .create(AuthenticationEntry(
                    userId = userId,
                    token = newToken))
        } else {
            // update if found
            authRepository.update(existedEntry.apply {
                token = newToken
            })
        }

        return newToken
    }
}