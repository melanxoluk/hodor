package ru.melanxoluk.hodor.common

data class UserAuth(
    val me: Me,
    val accessToken: String)