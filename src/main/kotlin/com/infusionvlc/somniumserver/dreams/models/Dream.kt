package com.infusionvlc.somniumserver.dreams.models

data class Dream(
  val id: Long = 0,
  val title: String = "",
  val description: String = "",
  val userId: Long = 0,
  val public: Boolean = true,
  val creationDate: Long = 0,
  val updateDate: Long = 0,
  val dreamtDate: Long = 0
)
