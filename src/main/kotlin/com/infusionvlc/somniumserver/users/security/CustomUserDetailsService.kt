package com.infusionvlc.somniumserver.users.security

import com.infusionvlc.somniumserver.users.security.models.toSecurity
import com.infusionvlc.somniumserver.users.usecases.FindUserByUsername
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
  private val findUserByUsername: FindUserByUsername
) : UserDetailsService {

  override fun loadUserByUsername(username: String): UserDetails =
    findUserByUsername.execute(username).toSecurity()
}
