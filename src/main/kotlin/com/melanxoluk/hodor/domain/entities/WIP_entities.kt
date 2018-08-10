package com.melanxoluk.hodor.domain.entities

import com.melanxoluk.hodor.domain.LongDomain


data class EmailPassword(
    override val id: Long = 0L,
    val email: String,
    val password: String,
    val userId: Long) : LongDomain<EmailPassword> {

    override fun inserted(id: Long) = copy(id = id)
}

data class EmailPasswordAuthentication(override val id: Long = 0L,
                                       val emailPasswordId: Long,
                                       val token: String,
                                       val clientId: Long
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
                            val applicationId: Long
                           ): LongDomain<PropertiesScheme> {
    override fun inserted(id: Long) = copy(id = id)
}

data class PropertiesSchemeEntry(override val id: Long = 0L,
                                 val schemeId: Long,
                                 val parentId: Long?,
                                 val name: String,
                                 val type: Type
                                ): LongDomain<PropertiesSchemeEntry> {

    // list of child entries for composite type
    val children: List<PropertiesSchemeEntry>? = null
    val parent: PropertiesSchemeEntry? = null

    override fun inserted(id: Long) = copy(id = id)
}


enum class HodorUserType {
    ADMIN, REGULAR
}

data class HodorUser(override val id: Long = 0L,
                     val userType: HodorUserType
                    ) : LongDomain<HodorUser> {

    var apps: List<App>? = null
    var emailPasswords: List<EmailPassword>? = null

    override fun inserted(id: Long) = copy(id = id)
}


data class AppUser(override val id: Long = 0L,
                   val applicationId: Long,
                   val properties: String,
                   val password: String,
                   val email: String
                  ): LongDomain<AppUser> {

    val app: App? = null
    val role: AppRole? = null

    override fun inserted(id: Long) = copy(id = id)
}


enum class UserType {
    HODOR, APP
}

data class AuthenticationEntry(override val id: Long = 0L,
                               val userType: UserType,
                               val userId: Long,
                               val token: String
                              ): LongDomain<AuthenticationEntry> {

    override fun inserted(id: Long) = copy(id = id)
}
