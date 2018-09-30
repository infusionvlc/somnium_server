package com.infusionvlc.somniumserver.users.security

import com.infusionvlc.somniumserver.users.models.UserNotFoundError
import com.infusionvlc.somniumserver.users.security.models.toSecurity
import com.infusionvlc.somniumserver.users.usecases.FindUserByUsername
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
  private val findUserByUsername: FindUserByUsername
) : UserDetailsService {

  override fun loadUserByUsername(username: String): UserDetails {
    val option = findUserByUsername.execute(username)
    if (option.isDefined()) {
      return option.get().toSecurity()
    } else {
      throw UserNotFoundError
    }
  }
}
