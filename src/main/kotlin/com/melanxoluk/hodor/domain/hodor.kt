package com.melanxoluk.hodor.domain

import com.melanxoluk.hodor.server.HodorConfig
import java.util.*


// fixme:
//   stop to embed uuids in sources. Decide how to handle situation
//   when after first starting application & creating that entities
//   such uuids makes lost and run application again


// ~~~ super user

val hodorSuperUsernamePassword =
    UsernamePassword(
        id = 0,
        username = HodorConfig.superUser.login,
        password = HodorConfig.superUser.password,
        userId = 0)

val hodorSuperUser =
    User(
        id = 0,
        applicationId = 0,
        properties = "",
        uuid = UUID.fromString("4ccd0b37-a0d0-423c-8a2f-796d85ee8528"))


// ~~~ hodor app

val hodorApp =
    Application(
        id = 0,
        creatorId = 0,
        name = "Hodor",
        uuid = UUID.fromString("036b0274-f6e5-4721-a5f6-fdf0efbb8e3f"))

val hodorWebClient =
    AppClient(
        id = 0,
        applicationId = 0,
        type = "web",
        uuid = UUID.fromString("fd1c662f-b196-43a6-a914-368458c1bb83"))
