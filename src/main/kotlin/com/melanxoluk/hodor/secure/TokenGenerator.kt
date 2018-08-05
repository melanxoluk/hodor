package com.melanxoluk.hodor.secure

import com.melanxoluk.hodor.common.UserRequestContext
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.MacProvider
import org.joda.time.DateTime
import org.koin.standalone.KoinComponent
import java.util.*


class TokenGenerator(private val key: String): KoinComponent {
    companion object {
        private const val CLIENT_CLAIM = "clt"
        private const val USER_CLAIM = "usr"
        private const val APP_CLAIM = "app"
    }


    @Deprecated("email is too small information about. Will removed")
    fun generate(email: String) =
        Jwts.builder()
            .claim("email", email)
            .setExpiration(DateTime().plusHours(1).toDate())

            // todo: differ keys for apps
            .signWith(SignatureAlgorithm.HS512, key)
            .compact()

    fun generate(context: UserRequestContext) =
        Jwts.builder()
            // default claims
            .setIssuedAt(Date())
            .setSubject(context.userLogin)

            // hodor specific claims
            .claim(CLIENT_CLAIM, context.clientUuid)
            .claim(USER_CLAIM, context.userUuid)
            .claim(APP_CLAIM, context.appUuid)

            .signWith(SignatureAlgorithm.HS512, key)
            .compact()
}
