package com.infusionvlc.somniumserver.users.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TokenAuthenticationFilter(
  private val tokenHelper: TokenHelper,
  private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {
  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    val token = tokenHelper.getToken(request)

    if (token != null) {
      val username = tokenHelper.getUsernameFromToken(token)
      val userDetails = userDetailsService.loadUserByUsername(username)
      val auth = TokenBasedAuthentication(userDetails, token)
      SecurityContextHolder.getContext().authentication = auth
    }

    filterChain.doFilter(request, response)
  }
}
