package com.infusionvlc.somniumserver.users.security

import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Configuration
@EnableWebMvc
class WebConfig(
  private val env: Environment
) : WebMvcConfigurerAdapter() {
  override fun addCorsMappings(registry: CorsRegistry) {
    registry.addMapping("/**")
      .allowedOrigins(env.getProperty("cors.allowedOrigin"))
      .allowedMethods("GET", "POST", "PUT", "DELETE")
  }
}
