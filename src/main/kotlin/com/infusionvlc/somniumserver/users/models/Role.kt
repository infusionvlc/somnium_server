package com.infusionvlc.somniumserver.users.models

data class Role (
  val id: Long,
  val name: String
) {
  companion object {
    fun userRole(): Role = Role(1, "user")
  }
}
