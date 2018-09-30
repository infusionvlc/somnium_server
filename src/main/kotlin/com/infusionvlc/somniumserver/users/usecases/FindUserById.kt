package com.infusionvlc.somniumserver.users.usecases

import arrow.core.Option
import com.infusionvlc.somniumserver.users.models.User
import com.infusionvlc.somniumserver.users.persistence.UserRepository
import com.infusionvlc.somniumserver.users.persistence.toDomain
import org.springframework.stereotype.Component

@Component
class FindUserById(private val dao: UserRepository) {
  fun execute(id: Long): Option<User> = Option
    .fromNullable(dao.findById(id).orElseGet { null })
    .map { it.toDomain() }
}
