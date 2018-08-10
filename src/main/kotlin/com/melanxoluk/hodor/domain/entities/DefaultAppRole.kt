package com.melanxoluk.hodor.domain.entities

import com.melanxoluk.hodor.domain.LongDomain


data class DefaultAppRole(
    override val id: Long = 0,
    val appId: Long,
    val roleId: Long)
    : LongDomain<DefaultAppRole> {

    override fun inserted(id: Long) = copy(id = id)
}
