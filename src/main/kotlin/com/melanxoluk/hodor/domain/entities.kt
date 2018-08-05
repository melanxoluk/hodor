package com.melanxoluk.hodor.domain

import java.util.*


data class UsernamePassword(
    override val id: Long,
    val username: String,
    val password: String,
    val userId: Long) : LongDomain<UsernamePassword> {

    override fun inserted(id: Long) = copy(id = id)
}

data class EmailPassword(
    override val id: Long = 0L,
    val email: String,
    val password: String,
    val userId: Long) : LongDomain<EmailPassword> {

    override fun inserted(id: Long) = copy(id = id)
}

data class EmailPasswordAuthentication(override val id: Long = 0L,
                                       val emailPasswordId: Long = 0L,
                                       val token: String = "",
                                       val clientId: Long = 0L
                                      ) : LongDomain<EmailPasswordAuthentication> {
    override fun inserted(id: Long) = copy(id = id)
}


enum class Type {
    BOOLEAN,
    NUMBER,

    LINE100,
    LINE250,
    TEXT,

    OBJECT
}

data class PropertiesScheme(override val id: Long = 0L,
                            val applicationId: Long = 0L
                           ): LongDomain<PropertiesScheme> {
    override fun inserted(id: Long) = copy(id = id)
}

data class PropertiesSchemeEntry(override val id: Long = 0L,
                                 val schemeId: Long = 0L,
                                 val parentId: Long? = null,
                                 val name: String = "",
                                 val type: Type = Type.LINE100
                                ): LongDomain<PropertiesSchemeEntry> {

    // list of child entries for composite type
    val children: List<PropertiesSchemeEntry>? = null
    val parent: PropertiesSchemeEntry? = null

    override fun inserted(id: Long) = copy(id = id)
}


data class User(override val id: Long = 0L,
                val applicationId: Long = 0L,
                val properties: String = "",
                val uuid: UUID = UUID.randomUUID()
               ) : LongDomain<User> {

    override fun inserted(id: Long) = copy(id = id)
}

enum class HodorUserType {
    ADMIN, REGULAR
}

data class HodorUser(override val id: Long = 0L,
                     val userType: HodorUserType = HodorUserType.REGULAR
                    ) : LongDomain<HodorUser> {

    var applications: List<Application>? = null
    var emailPasswords: List<EmailPassword>? = null

    override fun inserted(id: Long) = copy(id = id)
}


data class Application(override val id: Long = 0L,
                       val creatorId: Long = 0L,
                       val name: String = "",
                       val uuid: UUID = UUID.randomUUID()
                      ): LongDomain<Application> {

    val owner: HodorUser? = null

    override fun inserted(id: Long) = copy(id = id)
}


data class AppUser(override val id: Long = 0L,
                   val applicationId: Long = 0L,
                   val properties: String = "",
                   val password: String = "",
                   val email: String = ""
                          ): LongDomain<AppUser> {

    val application: Application? = null
    val role: ApplicationUserRole? = null

    override fun inserted(id: Long) = copy(id = id)
}

data class ApplicationUserRole(override val id: Long = 0L,
                               val applicationId: Long = 0L,
                               val role: String = ""
                              ): LongDomain<ApplicationUserRole> {

    val appUsers: List<AppUser>? = null
    val application: Application? = null

    override fun inserted(id: Long) = copy(id = id)
}


data class AppClient(
    override val id: Long = 0L,
    val applicationId: Long = 0L,
    val type: String = "",
    val uuid: UUID = UUID.randomUUID()
) : LongDomain<AppClient> {

    override fun inserted(id: Long) = copy(id = id)
}


enum class UserType {
    HODOR, APP
}

data class AuthenticationEntry(override val id: Long = 0L,
                               val userType: UserType = UserType.APP,
                               val userId: Long = 0L,
                               val token: String = ""
                              ): LongDomain<AuthenticationEntry> {

    override fun inserted(id: Long) = copy(id = id)
}
