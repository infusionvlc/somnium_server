package com.infusionvlc.somniumserver.users.security.models

import org.springframework.security.core.GrantedAuthority

data class Authority(
  val id: Long,
  val name: String
) : GrantedAuthority {
  override fun getAuthority(): String = name
}
