package com.infusionvlc.somniumserver.users.usecases

import arrow.core.Option
import com.infusionvlc.somniumserver.users.models.User
import com.infusionvlc.somniumserver.users.persistence.UserRepository
import com.infusionvlc.somniumserver.users.persistence.toDomain
import org.springframework.stereotype.Component

@Component
class FindUserByUsername(
  private val dao: UserRepository
) {
  fun execute(username: String): Option<User> = Option
    .fromNullable(dao.findByUsername(username))
    .map { it.toDomain() }
}
