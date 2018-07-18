package com.melanxoluk.hodor.secure

import java.security.MessageDigest


class PasswordHasher(private val salt: ByteArray) {
    // todo create most secure hashing function
    fun hash(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-512") // todo config value
        messageDigest.update(salt)
        val hashBytes = messageDigest.digest(password.toByteArray())
        return String(hashBytes)
    }
}