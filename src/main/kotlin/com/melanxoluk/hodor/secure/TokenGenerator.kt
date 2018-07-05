package com.melanxoluk.hodor.secure

import java.util.*


class TokenGenerator {
    // todo: determine most secure way to build such tokens
    fun generate() = UUID.randomUUID().toString()


    companion object {
        private val digits = "0123456789"
        private val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private val lower = upper.toLowerCase(Locale.ROOT)

        // whole set of all regular characters
        private val alphanum = upper + lower + digits
    }
}