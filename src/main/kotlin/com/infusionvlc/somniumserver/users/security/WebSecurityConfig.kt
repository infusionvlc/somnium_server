package com.infusionvlc.somniumserver.users.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
  private val userDetailsService: CustomUserDetailsService,
  private val tokenHelper: TokenHelper,
  private val unauthorizedHandler: JwtAuthenticationEntryPoint
) : WebSecurityConfigurerAdapter() {

  @Bean
  fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

  @Bean(name = [(BeanIds.AUTHENTICATION_MANAGER)])
  override fun authenticationManagerBean(): AuthenticationManager {
    return super.authenticationManagerBean()
  }

  @Autowired
  fun configureGlobal(auth: AuthenticationManagerBuilder) {
    auth.userDetailsService(userDetailsService)
      .passwordEncoder(passwordEncoder())
  }

  override fun configure(http: HttpSecurity) {
    http
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
      .authorizeRequests()
      .antMatchers("/auth/**").permitAll()
      .antMatchers("/v2/api-docs/**").permitAll()
      .antMatchers("/swagger**/**").permitAll()
      .antMatchers("/webjars/**").permitAll()
      .antMatchers("/h2-console/**").permitAll()
      .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
      .anyRequest().authenticated()
      .and()
      .headers().frameOptions().sameOrigin()
      .and()
      .addFilterBefore(TokenAuthenticationFilter(tokenHelper, userDetailsService),
        UsernamePasswordAuthenticationFilter::class.java)
      .csrf().disable()
      .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
  }

}
