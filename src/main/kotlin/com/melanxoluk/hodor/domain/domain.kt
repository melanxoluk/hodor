package com.melanxoluk.hodor.domain

import java.util.*


interface Domain

interface IdDomain<ID: Comparable<ID>, D: IdDomain<ID, D>>
    : Domain, Comparable<IdDomain<ID, D>> {

    val id: ID

    fun inserted(id: ID): D
}

interface IntDomain<IntD: IdDomain<Int, IntD>>: IdDomain<Int, IntD> {
    override fun compareTo(other: IdDomain<Int, IntD>) = id.compareTo(other.id)
}
interface LongDomain<LongD: IdDomain<Long, LongD>>: IdDomain<Long, LongD> {
    override fun compareTo(other: IdDomain<Long, LongD>) = id.compareTo(other.id)
}
interface UUIDDomain<UUIDD: IdDomain<UUID, UUIDD>>: IdDomain<UUID, UUIDD> {
    override fun compareTo(other: IdDomain<UUID, UUIDD>) = id.compareTo(other.id)
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
