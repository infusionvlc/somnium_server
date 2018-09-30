package com.infusionvlc.somniumserver.users.usecases

import com.infusionvlc.somniumserver.users.models.RegisterRequest
import com.infusionvlc.somniumserver.users.models.Role
import com.infusionvlc.somniumserver.users.models.User
import com.infusionvlc.somniumserver.users.persistence.UserRepository
import com.infusionvlc.somniumserver.users.persistence.toDomain
import com.infusionvlc.somniumserver.users.persistence.toEntity
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class RegisterUser(
  private val dao: UserRepository
) {

  @Bean
  fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

  fun execute(registerRequest: RegisterRequest): User {
    val (username, password) = registerRequest
    val userEntity = User(
      0,
      username,
      passwordEncoder().encode(password),
      Role.userRole()
    ).toEntity()

    val result = dao.save(userEntity)
    return result.toDomain()
  }
}
