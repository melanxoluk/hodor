package com.melanxoluk.hodor.domain.entities

import com.melanxoluk.hodor.domain.HodorUser
import com.melanxoluk.hodor.domain.LongDomain
import java.util.*


data class App(override val id: Long = 0L,
               val creatorId: Long,
               val name: String,
               val uuid: UUID
              ): LongDomain<App> {

    val owner: HodorUser? = null

    override fun inserted(id: Long) = copy(id = id)
}