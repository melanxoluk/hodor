package com.melanxoluk.hodor.services

import com.melanxoluk.hodor.common.Negative
import com.melanxoluk.hodor.common.Positive
import com.melanxoluk.hodor.common.UsernameLogin
import com.melanxoluk.hodor.domain.context.UsernameContext
import com.melanxoluk.hodor.secure.PasswordHasher
import com.melanxoluk.hodor.secure.TokenService
import org.koin.core.component.get


class Token(val token: String)

class SimpleLoginService: Service() {
    private val tokenGenerator = get<TokenService>()
    private val hasher = get<PasswordHasher>()

    fun simpleLogin(login: UsernameLogin) = ok<Token> {
        val usernameRes = usernameContextRepository.getOrCreate(login)
        return@ok when(usernameRes) {
            is Positive -> {
                val isCorrect = checkPassword(login, usernameRes.body)
                if (isCorrect) {
                    Token(tokenGenerator.generate(usernameRes.body))
                } else {
                    clientError("Wrong credentials")
                }
            }
            is Negative -> {
                clientError(usernameRes.exceptionMessage)
            }
            else -> throw IllegalStateException()
        }
    }

    private fun checkPassword(login: UsernameLogin,
                              context: UsernameContext): Boolean {
        val originalHash = hasher.hash(login.password)
        return originalHash == context.password
    }
}

//class AuthService: Service {
//    companion object {
//        private val NOT_FOUND_EMAIL = "User with such email not registered"
//        private val WRONG_PASSWORD = "Wrong password"
//        private val NOT_AUTH = "Not authenticated"
//    }
//
//    private val usernamePasswordsRepository = get<UsernamePasswordsRepository>()
//    private val usersService = get<UsersService>()
//
//    private val passwordHasher = get<PasswordHasher>()
//    private val tokenGenerator = get<TokenGenerator>()
//
//
//    // ~~~ username/password flow
//
//    fun simpleUsernameLogin(login: UsernameLogin) = ok {
//        // todo: add some validation rules here? pass length
//
//        // when returned user should be available from
//        val usernamePassword = usersService.getOrCreate(login)
//        val user = usernamePassword.user!!
//
//        // pass hash check
//        val consumedHash = passwordHasher.hash(login.password)
//        val storedHash = emailPass.password
//        if (consumedHash != storedHash) {
//            return@ok clientError<String>(WRONG_PASSWORD)
//        }
//
//        // we need new token for user
//        val newToken = tokenGenerator.generate(login.username)
//
//        // and check previous entry if not user
//        if (newEmail) {
//            emailPassAuthRepository.create(
//                    EmailPasswordAuthentication(
//                            emailPasswordId = emailPass.id,
//                            token = newToken))
//        } else {
//            val authEntry = emailPassAuthRepository.findByEmailPasswordId(emailPass.id)
//            if (authEntry == null) {
//                emailPassAuthRepository.create(
//                        EmailPasswordAuthentication(
//                                emailPasswordId = emailPass.id,
//                                token = newToken))
//            } else {
//                emailPassAuthRepository.update(authEntry.copy(
//                    token = newToken
//                ))
//            }
//        }
//
//        newToken
//    }
//
//
//    // ~~~ hodor users flow
//
//    /*fun loginHodorUser(email: String,
//                       password: String): ServiceResult<LoginedHodorUser> {
//
//        // email check
//        val hodorUser = hodorUsersRepository.findByEmail(email)
//            ?: return clientError(NOT_FOUND_EMAIL)
//
//        // pass hash check
//        val consumedHash = passwordHasher.hash(password)
//        val storedHash = hodorUser.password
//        if (consumedHash != storedHash) {
//            return clientError(WRONG_PASSWORD)
//        }
//
//        // checks are processed, return token
//        return ok {
//            val token = refreshToken(email, hodorUser.id, UserType.HODOR)
//            LoginedHodorUser(hodorUser.userType, token)
//        }
//    }
//
//    fun getHodorUser(token: String): ServiceResult<HodorUser> {
//        val authEntry = authRepository.findByToken(token)
//            ?: return clientError(NOT_AUTH)
//
//        return ok { hodorUsersRepository.read(authEntry.userId) }
//    }*/
//
//
//    // ~~~ app users flow
//
//    /*fun loginApplicationUser(appId: Long,
//                             email: String,
//                             password: String): ServiceResult<String> {
//
//        // email check
//        val appUser = appUsersRepository.findUser(appId, email)
//            ?: return clientError(NOT_FOUND_EMAIL)
//
//        // pass hash check
//        val consumedHash = passwordHasher.hash(password)
//        val storedHash = appUser.password
//        if (consumedHash != storedHash) {
//            return clientError(WRONG_PASSWORD)
//        }
//
//        // check are processed, return token
//        return ok { refreshToken(email, appUser.id, UserType.APP) }
//    }
//
//    fun getAppUser(token: String): ServiceResult<AppUser> {
//        val authEntry = authRepository.findByToken(token)
//            ?: return clientError(NOT_AUTH)
//
//        return ok { appUsersRepository.read(authEntry.userId) }
//    }*/
//
//
//    // ~~~ misc
//
//    private fun refreshToken(email: String, userId: Long, userType: UserType): String {
//        // we need new token for user
//        val newToken = tokenGenerator.generate(email)
//
//        // and check previous entry
//        val existedEntry = if (userType == UserType.HODOR) {
//            authRepository.findByHodorUser(userId)
//        } else {
//            authRepository.findByAppUser(userId)
//        }
//
//        // create new if not found
//        if (existedEntry == null) {
//            authRepository
//                .create(AuthenticationEntry(
//                        userId = userId,
//                        token = newToken))
//        } else {
//            // update if found
//            authRepository.update(existedEntry.copy(
//                token = newToken
//            ))
//        }
//
//        return newToken
//    }
//}