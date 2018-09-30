package com.infusionvlc.somniumserver.users.security.models

import com.infusionvlc.somniumserver.users.models.Role
import com.infusionvlc.somniumserver.users.models.User

fun User.toSecurity(): SecurityUser = SecurityUser(
  id,
  username,
  password,
  listOf(role.toAuthority())
)

fun Role.toAuthority(): Authority = Authority(
  id,
  name
)
