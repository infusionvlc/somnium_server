package com.infusionvlc.somniumserver.users.security.models

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class SecurityUser(
  val id: Long = 0,
  private val username: String = "",
  private val password: String = "",
  private val roles: List<Authority> = emptyList()
) : UserDetails {

  override fun getAuthorities(): MutableCollection<out GrantedAuthority> = roles.toMutableList()

  override fun isEnabled(): Boolean = true

  override fun getUsername(): String = username

  override fun isCredentialsNonExpired(): Boolean = true

  override fun getPassword(): String = password

  override fun isAccountNonExpired(): Boolean = true

  override fun isAccountNonLocked(): Boolean = true
}
