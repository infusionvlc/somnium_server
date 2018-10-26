package com.infusionvlc.somniumserver.users.models

data class User(
  val id: Long,
  val username: String,
  val password: String,
  val followings: List<Long>,
  val followers: List<Long>,
  val role: Role = Role.userRole()
)
