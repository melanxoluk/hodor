package com.melanxoluk.hodor.domain.entities

import com.melanxoluk.hodor.domain.LongDomain
import java.util.*


data class AppClient(
    override val id: Long = 0L,
    val appId: Long,
    val type: String,
    val uuid: UUID
) : LongDomain<AppClient> {

    override fun inserted(id: Long) = copy(id = id)
}