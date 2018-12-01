package com.infusionvlc.somniumserver.users.usecases

import arrow.core.Either
import arrow.core.fix
import arrow.core.left
import arrow.core.monad
import arrow.typeclasses.binding
import com.infusionvlc.somniumserver.base.toEither
import com.infusionvlc.somniumserver.users.persistence.UserDAO
import org.springframework.stereotype.Component

@Component
class FollowUser(
  private val userDAO: UserDAO
) {

  sealed class Errors {
    object PersistenceError : Errors()
    object UserNotFound : Errors()
    object FollowingUserAlready : Errors()
    object FollowingMyself : Errors()
  }

  fun execute(targetUserId: Long, myUserId: Long): Either<Errors, Unit> =
    Either.monad<Errors>().binding {
      val targetUser = userDAO.findById(targetUserId)
        .toEither { Errors.UserNotFound }.bind()
      val myUser = userDAO.findById(myUserId)
        .toEither { Errors.PersistenceError }.bind()
      if (amIFollowingTargetUser(targetUser, myUser))
        userDAO.addFollowersRelation(targetUser, myUser)
          .toEither { Errors.PersistenceError }.bind()
    }.fix()
}
