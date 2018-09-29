package com.infusionvlc.somniumserver.users.security.models

import com.infusionvlc.somniumserver.users.models.Role
import com.infusionvlc.somniumserver.users.models.User
import org.springframework.security.core.userdetails.UserDetails

fun User.toSecurity(): SecurityUser = SecurityUser(
  username,
  password,
  listOf(role.toAuthority())
)

fun Role.toAuthority(): Authority = Authority(
  id,
  name
)
