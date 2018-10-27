package com.infusionvlc.somniumserver.users.persistence

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface UserRepository : CrudRepository<UserEntity, Long> {
  fun findByUsername(username: String): UserEntity?

  @Query("SELECT target FROM UserEntity user INNER JOIN user.following target " +
    "WHERE target.id = :targetUserId AND user.id = :userId")
  fun findTargetUserInUserFollowing(targetUserId: Long, userId: Long): Optional<UserEntity>
}
