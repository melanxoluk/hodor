package com.melanxoluk.hodor.common

import java.util.*


class UserRequestContext(
    val userId: Long,
    val userUuid: UUID,
    val userLogin: String, // phone, email, username
    val clientId: Long,
    val clientUuid: UUID,
    val appId: Long,
    val appUuid: UUID)
