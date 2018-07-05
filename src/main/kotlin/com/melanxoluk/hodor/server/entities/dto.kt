package com.melanxoluk.hodor.server.entities

import com.melanxoluk.hodor.domain.HodorUserType


data class LoginedHodorUser(val hodorUserType: HodorUserType,
                            val token: String)


class ErrorResponse private constructor(val code: Int,
                                        val message: String) {
    companion object {
        // general type of login exception
        val NOT_LOGINED = ErrorResponse(1, "Not identified")
    }
}