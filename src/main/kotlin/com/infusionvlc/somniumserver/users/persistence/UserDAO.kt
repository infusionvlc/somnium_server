package com.infusionvlc.somniumserver.users.persistence

import arrow.core.Option
import arrow.core.Try
import arrow.core.identity
import arrow.core.toOption
import com.infusionvlc.somniumserver.base.toOption
import com.infusionvlc.somniumserver.users.models.User
import org.springframework.stereotype.Component

@Component
class UserDAO(private val localDatasource: UserRepository) {

  fun findById(id: Long): Option<User> =
    localDatasource.findById(id)
      .toOption()
      .map(UserEntity::toDomain)

  fun findByUsername(username: String): Option<User> =
    localDatasource.findByUsername(username)
      .toOption()
      .map(UserEntity::toDomain)

  fun saveUser(user: User): Try<User> = Try {
    val userEntity = localDatasource.findById(user.id)
      .toOption()
      .map {
        it.copy(
          username = user.username,
          password = user.password
        )
      }
      .fold({ throw Exception("Persistence error") }, ::identity)

    localDatasource.save(userEntity)
  }
    .map(UserEntity::toDomain)

  fun addFollowersRelation(targetUser: User, myUser: User): Try<User> = Try {
    localDatasource.findById(targetUser.id).toOption()
      .flatMap { targetUserEntity ->
        localDatasource.findById(myUser.id).toOption()
          .map { myUserEntity ->
            val result = targetUserEntity.copy(followers = targetUserEntity.followers + myUserEntity)
            myUserEntity.copy(following = myUserEntity.following + targetUserEntity)

            result.toDomain()
          }
      }.fold({ throw Exception("User not found") }, ::identity)
  }

  fun deleteUser(user: User): Try<Boolean> = Try {
    val userEntity = localDatasource.findById(user.id)
      .toOption()
      .fold({ throw Exception("Persistence error") }, ::identity)

    localDatasource.delete(userEntity)
    true
  }
}
