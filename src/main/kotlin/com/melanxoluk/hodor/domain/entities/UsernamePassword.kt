package com.melanxoluk.hodor.domain.entities

import com.melanxoluk.hodor.domain.LongDomain


data class UsernamePassword(
    override val id: Long = 0L,
    val username: String,
    val password: String,
    val userId: Long) : LongDomain<UsernamePassword> {

    var user: User? = null

    override fun inserted(id: Long) = copy(id = id)
}