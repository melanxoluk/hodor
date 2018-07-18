package com.melanxoluk.hodor.domain


data class EmailPassword(override var id: Long = 0L,
                         var email: String,
                         var password: String): LongDomain<EmailPassword> {

    override fun inserted(id: Long) = copy(id = id)
}

data class EmailPasswordAuthentication(override var id: Long = 0L,
                                       var emailPasswordId: Long = 0L,
                                       var token: String = "") : LongDomain<EmailPasswordAuthentication> {
    override fun inserted(id: Long) = copy(id = id)
}


enum class HodorUserType {
    ADMIN, REGULAR
}

data class HodorUser(override var id: Long = 0L,
                     var userType: HodorUserType = HodorUserType.REGULAR,
                     var password: String = "",
                     var email: String = ""
                    ): LongDomain<HodorUser> {

    var applications: List<Application>? = null

    override fun inserted(id: Long) = copy(id = id)
}


data class Application(override var id: Long = 0L,
                       var creatorId: Long = 0L,
                       var token: String = "",
                       var name: String = ""
                      ): LongDomain<Application> {

    var owner: HodorUser? = null

    override fun inserted(id: Long) = copy(id = id)
}


data class ApplicationUser(override var id: Long = 0L,
                           var applicationId: Long = 0L,
                           var properties: String = "",
                           var password: String = "",
                           var email: String = ""
                          ): LongDomain<ApplicationUser> {

    var application: Application? = null
    var role: ApplicationUserRole? = null

    override fun inserted(id: Long) = copy(id = id)
}

data class ApplicationUserRole(override var id: Long = 0L,
                               var applicationId: Long = 0L,
                               var role: String = ""
                              ): LongDomain<ApplicationUserRole> {

    var applicationUsers: List<ApplicationUser>? = null
    var application: Application? = null

    override fun inserted(id: Long) = copy(id = id)
}


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
