package com.infusionvlc.somniumserver.users.usecases

import com.infusionvlc.somniumserver.users.models.LoginRequest
import com.infusionvlc.somniumserver.users.security.TokenHelper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class LoginUser(
  private val authenticationManager: AuthenticationManager,
  private val tokenHelper: TokenHelper
) {
  fun execute(loginRequest: LoginRequest): String {
    val (username, password) = loginRequest
    val authentication = authenticationManager.authenticate(
      UsernamePasswordAuthenticationToken(username, password)
    )

    SecurityContextHolder.getContext().authentication = authentication

    return tokenHelper.generateToken(username)
  }
}
