package com.infusionvlc.somniumserver.users.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.base.toEither
import com.infusionvlc.somniumserver.users.models.User
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

  fun execute(targetUserId: Long, myUserId: Long): Either<Errors, User> =
    userDAO.findById(targetUserId)
      .toEither { Errors.UserNotFound }
      .flatMap { targetUser ->
        userDAO.findById(myUserId)
          .toEither { Errors.PersistenceError }
          .flatMap { myUser ->
            amIFollowingTargetUser(targetUser, myUser)
              .flatMap {
                amIFollowingMyself(targetUser, myUser)
                  .flatMap {
                    userDAO.addFollowersRelation(targetUser, myUser)
                      .toEither { Errors.PersistenceError }
                  }
              }
          }
      }

  private fun amIFollowingMyself(targetUser: User, myUser: User): Either<Errors, User> =
    if (targetUser.id == myUser.id) Errors.FollowingMyself.left() else targetUser.right()

  private fun amIFollowingTargetUser(targetUser: User, myUser: User): Either<Errors, User> =
    userDAO.isUserFollowingTarget(targetUser, myUser)
      .toEither { Errors.PersistenceError }
      .flatMap {
        if (it) {
          Errors.FollowingUserAlready.left()
        } else {
          targetUser.right()
        }
      }
}
