package com.melanxoluk.hodor.domain

import com.melanxoluk.hodor.server.HodorConfig
import java.util.*


// ~~~ super user

val superEmailPassword =
    EmailPassword(
        0,
        HodorConfig.superUser.login,
        HodorConfig.superUser.password,
        0)

val superEmailPasswordAuth =
    EmailPasswordAuthentication(
        0,
        0,
        "5de05b50-521b-4f91-b2b1-51012f4befbe",
        0)

val superUser =
    User(
        0,
        0,
        "",
        UUID.fromString("4ccd0b37-a0d0-423c-8a2f-796d85ee8528"))


// ~~~ hodor app

val hodorApp =
    Application(
        0,
        0,
        "Hodor",
        UUID.fromString("036b0274-f6e5-4721-a5f6-fdf0efbb8e3f"))

val hodorWebClient =
    ApplicationClient(
        0,
        0,
        "web",
        UUID.fromString("fd1c662f-b196-43a6-a914-368458c1bb83"))
