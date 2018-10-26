package com.infusionvlc.somniumserver.users.usecases

import arrow.core.identity
import com.infusionvlc.somniumserver.users.models.RegisterRequest
import com.infusionvlc.somniumserver.users.models.Role
import com.infusionvlc.somniumserver.users.models.User
import com.infusionvlc.somniumserver.users.persistence.UserDAO
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
class RegisterUser(
  private val dao: UserDAO
) {

  @Bean
  fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

  fun execute(registerRequest: RegisterRequest): User {
    val (username, password) = registerRequest
    val userEntity = User(
      0,
      username,
      passwordEncoder().encode(password),
      emptyList(),
      emptyList(),
      Role.userRole()
    )

    val result = dao.saveUser(userEntity)
    return result.fold({ throw Exception("Persistence exception") }, ::identity)
  }
}
