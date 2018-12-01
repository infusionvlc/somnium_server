package com.infusionvlc.somniumserver.users.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.users.models.User

fun amIFollowingMyself(targetUser: User, myUser: User): Boolean =
  targetUser.id == myUser.id


fun amIFollowingTargetUser(targetUser: User, myUser: User): Boolean =
  myUser.followings.find { it == targetUser.id } == null


