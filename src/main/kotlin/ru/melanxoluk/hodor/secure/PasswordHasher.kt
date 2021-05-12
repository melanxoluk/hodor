package ru.melanxoluk.hodor.secure

import org.koin.core.component.KoinComponent
import java.security.MessageDigest


class PasswordHasher(private val salt: ByteArray): KoinComponent {
    // todo create most secure hashing function
    fun hash(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-512") // todo config value
        messageDigest.update(salt)
        val hashBytes = messageDigest.digest(password.toByteArray())
        return String(hashBytes)
    }
}