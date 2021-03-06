package com.infusionvlc.somniumserver.dreams.models

data class DreamRequest(
  val title: String = "",
  val description: String = "",
  val dreamtDate: Long = 0
)

fun DreamRequest.toDomain(userId: Long): Dream = Dream(
  id = 0,
  title = this.title,
  description = this.description,
  userId = userId,
  creationDate = System.currentTimeMillis(),
  updateDate = System.currentTimeMillis(),
  dreamtDate = this.dreamtDate
)
