package com.melanxoluk.hodor.secure

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.MacProvider
import org.joda.time.DateTime
import java.util.*


class TokenGenerator(private val key: String) {
    fun generate(email: String) =
        Jwts.builder()
            .claim("email", email)
            .setExpiration(DateTime().plusHours(1).toDate())
            .signWith(SignatureAlgorithm.HS512, key)
            .compact()
}

fun main(args: Array<String>) {
    val encoded = MacProvider.generateKey().encoded
    print(Base64.getEncoder().encode(encoded))
}