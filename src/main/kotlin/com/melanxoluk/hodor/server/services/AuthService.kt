package com.melanxoluk.hodor.server.services

import com.melanxoluk.hodor.domain.*
import com.melanxoluk.hodor.secure.PasswordHasher
import com.melanxoluk.hodor.secure.TokenGenerator
import com.melanxoluk.hodor.server.storage.repositories.*
import org.koin.standalone.get


class AuthService: Service {
    companion object {
        private val NOT_FOUND_EMAIL = "User with such email not registered"
        private val WRONG_PASSWORD = "Wrong password"
        private val NOT_AUTH = "Not authenticated"
    }

    private val emailPassAuthRepository = get<EmailPasswordsAuthRepository>()
    private val emailPassRepository = get<EmailPasswordsRepository>()

    private val hodorUsersRepository = get<HodorUsersRepository>()
    private val appUsersRepository = get<AppUsersRepository>()
    private val passwordHasher = get<PasswordHasher>()
    private val authRepository = get<AuthRepository>()
    private val tokenGenerator = get<TokenGenerator>()


    // ~~~ email passwords flow
    
    // returns token new token if successful or error if not 
    fun simpleLogin(password: String, email: String, client: Long) = ok {
        // todo: add some validation rules here? pass length, email is email

        var newEmail = false
        var emailPass = emailPassRepository.findByEmail(email)
        if (emailPass == null) {
            newEmail = true

            // need to create new regular hodor user before
            val newHodorUser = hodorUsersRepository.create(HodorUser())
            emailPass = emailPassRepository.create(
                EmailPassword(
                    email = email,
                    password = passwordHasher.hash(password),
                    userId = newHodorUser.id))
        }

        // pass hash check
        val consumedHash = passwordHasher.hash(password)
        val storedHash = emailPass.password
        if (consumedHash != storedHash) {
            return@ok clientError<String>(WRONG_PASSWORD)
        }

        // we need new token for user
        val newToken = tokenGenerator.generate(email)

        // and check previous entry if not user
        if (newEmail) {
            emailPassAuthRepository.create(
                EmailPasswordAuthentication(
                    emailPasswordId = emailPass.id,
                    token = newToken))
        } else {
            val authEntry = emailPassAuthRepository.findByEmailPasswordId(emailPass.id)
            if (authEntry == null) {
                emailPassAuthRepository.create(
                    EmailPasswordAuthentication(
                        emailPasswordId = emailPass.id,
                        token = newToken))
            } else {
                emailPassAuthRepository.update(authEntry.copy(
                    token = newToken
                ))
            }
        }

        newToken
    }
    

    // ~~~ hodor users flow

    /*fun loginHodorUser(email: String,
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
            val token = refreshToken(email, hodorUser.id, UserType.HODOR)
            LoginedHodorUser(hodorUser.userType, token)
        }
    }

    fun getHodorUser(token: String): ServiceResult<HodorUser> {
        val authEntry = authRepository.findByToken(token)
            ?: return clientError(NOT_AUTH)

        return ok { hodorUsersRepository.read(authEntry.userId) }
    }*/


    // ~~~ app users flow

    /*fun loginApplicationUser(appId: Long,
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
        return ok { refreshToken(email, appUser.id, UserType.APP) }
    }

    fun getAppUser(token: String): ServiceResult<AppUser> {
        val authEntry = authRepository.findByToken(token)
            ?: return clientError(NOT_AUTH)

        return ok { appUsersRepository.read(authEntry.userId) }
    }*/


    // ~~~ misc

    private fun refreshToken(email: String, userId: Long, userType: UserType): String {
        // we need new token for user
        val newToken = tokenGenerator.generate(email)

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
            authRepository.update(existedEntry.copy(
                token = newToken
            ))
        }

        return newToken
    }
}