package com.infusionvlc.somniumserver.users.persistence

import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Long> {
  fun findByUsername(username: String): UserEntity?
}
