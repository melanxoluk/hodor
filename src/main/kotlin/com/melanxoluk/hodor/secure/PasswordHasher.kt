package com.melanxoluk.hodor.secure

import java.security.MessageDigest


class PasswordHasher(val salt: ByteArray) {
    private val messageDigest: MessageDigest

    init {
        messageDigest = MessageDigest.getInstance("SHA-512") // todo config value
        messageDigest.update(salt)
    }


    // todo create most secure hashing function
    fun hash(password: String): String {
        val hashBytes = messageDigest.digest(password.toByteArray())
        return String(hashBytes)
    }
}