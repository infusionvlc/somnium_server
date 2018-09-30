package com.infusionvlc.somniumserver.users.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class TokenBasedAuthentication(
  private val userDetails: UserDetails,
  val token: String
) : AbstractAuthenticationToken(userDetails.authorities) {
  override fun getCredentials() = token
  override fun getPrincipal() = userDetails
  override fun isAuthenticated(): Boolean = true
}
