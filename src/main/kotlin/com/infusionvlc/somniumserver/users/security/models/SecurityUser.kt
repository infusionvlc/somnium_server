package com.infusionvlc.somniumserver.users.security.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class SecurityUser(
  private val name: String = "",
  private val pswd: String = "",
  private val roles: List<Authority> = emptyList()
) : UserDetails {

  override fun getAuthorities(): MutableCollection<out GrantedAuthority> = roles.toMutableList()

  override fun isEnabled(): Boolean = true

  override fun getUsername(): String = name

  override fun isCredentialsNonExpired(): Boolean = true

  override fun getPassword(): String = pswd

  override fun isAccountNonExpired(): Boolean = true

  override fun isAccountNonLocked(): Boolean = true
}
