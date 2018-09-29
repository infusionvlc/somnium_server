package com.infusionvlc.somniumserver.users.models

data class UserResponse(
  val id: Long,
  val username: String
)

fun User.toResponse(): UserResponse = UserResponse(id, username)
