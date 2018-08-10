package com.melanxoluk.hodor.domain

import com.melanxoluk.hodor.domain.entities.*
import com.melanxoluk.hodor.server.HodorConfig
import java.util.*


// fixme:
//   stop to embed uuids in sources. Decide how to handle situation
//   when after first starting app & creating that entities
//   such uuids makes lost and run app again


// fixme:
//   hodor entities are belong to hodor user, user who's properties
//   are hodor prefix. No more users should be able to has such properties
const val hodorPrefix = "__hodor__"


// ~~~ super user

var hodorSuperUsernamePassword =
    UsernamePassword(
        id = 0,
        username = HodorConfig.superUser.login,
        password = HodorConfig.superUser.password,
        userId = 0)
    internal set

var hodorSuperUser =
    User(
        id = 0,
        appId = 0,
        properties = hodorPrefix,
        uuid = UUID.fromString("4ccd0b37-a0d0-423c-8a2f-796d85ee8528"))
    internal set


// ~~~ hodor app

var hodorApp =
    App(
        id = 0,
        name = hodorPrefix,
        uuid = UUID.fromString("036b0274-f6e5-4721-a5f6-fdf0efbb8e3f"))
    internal set

var hodorClient =
    AppClient(
        id = 0,
        appId = 0,
        type = "web",
        uuid = UUID.fromString("fd1c662f-b196-43a6-a914-368458c1bb83"))
    internal set


// ~~~ hodor roles

var hodorAdminRole =
    AppRole(
        id = 0,
        appId = 0,
        uuid = UUID.fromString("9bf49948-7100-4fb6-977a-ec26cf9e8820"),
        name = "admin")
    internal set

var hodorUserRole =
    AppRole(
        id = 0,
        appId = 0,
        uuid = UUID.fromString("daf5b91b-e41f-4e2a-8b71-ace61c7dbf65"),
        name = "user")
    internal set

var hodorRoles = listOf(hodorAdminRole, hodorUserRole)
    internal set
