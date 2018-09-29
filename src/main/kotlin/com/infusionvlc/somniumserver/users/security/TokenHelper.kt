package com.infusionvlc.somniumserver.users.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class TokenHelper {

  companion object {
    private const val APP_NAME = "somnium"
    private const val SECRET = "catchme:ifyou_can"
    private val SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512
    const val HEADER = "Authorization"
    const val EXPIRES_IN = 604800L
  }

  fun getUsernameFromToken(token: String) =
    getClaimsFromToken(token, Claims::getSubject)

  fun canTokenBeRefreshed(token: String): Boolean {
    val expirationDate = getClaimsFromToken(token, Claims::getExpiration)
    return expirationDate > generateCurrentDate()
  }

  fun refreshToken(token: String): String {
    val claims = getAllClaimsFromToken(token)
    claims.issuedAt = generateCurrentDate()
    return generateToken(claims)
  }

  fun generateToken(username: String): String =
    Jwts.builder()
      .setIssuer(APP_NAME)
      .setSubject(username)
      .setIssuedAt(generateCurrentDate())
      .setExpiration(generateExpirationDate())
      .signWith(SIGNATURE_ALGORITHM, SECRET)
      .compact()

  fun getToken(request: HttpServletRequest): String? {
    val authHeader = request.getHeader(HEADER)

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7)
    }

    return null
  }

  private inline fun <T> getClaimsFromToken(token: String, claimsResolver: (Claims) -> T): T {
    val claims = getAllClaimsFromToken(token)
    return claimsResolver(claims)
  }

  private fun getAllClaimsFromToken(token: String): Claims =
    Jwts.parser()
      .setSigningKey(SECRET)
      .parseClaimsJws(token)
      .body

  private fun generateToken(claims: Map<String, Any>): String {
    return Jwts.builder()
      .setClaims(claims)
      .setExpiration(generateExpirationDate())
      .signWith(SIGNATURE_ALGORITHM, SECRET)
      .compact()
  }

  private fun generateCurrentDate() = Date(System.currentTimeMillis())
  private fun generateExpirationDate() = Date(System.currentTimeMillis() + EXPIRES_IN * 1000)
}
