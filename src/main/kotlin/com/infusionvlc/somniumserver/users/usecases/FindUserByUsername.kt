package com.infusionvlc.somniumserver.users.usecases

import com.infusionvlc.somniumserver.users.models.Role
import com.infusionvlc.somniumserver.users.models.User
import org.springframework.stereotype.Component

@Component
class FindUserByUsername {

  fun execute(username: String): User = User(
    1,
    "test",
    "qwerty",
    Role(1, "user"))

}
