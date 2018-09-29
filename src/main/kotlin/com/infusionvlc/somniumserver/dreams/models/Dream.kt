package com.infusionvlc.somniumserver.dreams.models

data class Dream(
  val id: Long,
  val title: String,
  val description: String,
  val userId: Long,
  val creationDate: Long,
  val updateDate: Long,
  val dreamtDate: Long
)
