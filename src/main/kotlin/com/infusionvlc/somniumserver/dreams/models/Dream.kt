package com.infusionvlc.somniumserver.dreams.models

import com.infusionvlc.somniumserver.tags.models.Tag

data class Dream(
  val id: Long,
  val title: String,
  val description: String,
  val userId: Long,
  val public: Boolean,
  val tags: MutableList<Tag> = mutableListOf(),
  val creationDate: Long,
  val updateDate: Long,
  val dreamtDate: Long
)
