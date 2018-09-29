package com.infusionvlc.somniumserver.users.models

data class LoginRequest(
  val username: String = "",
  val password: String = ""
)
