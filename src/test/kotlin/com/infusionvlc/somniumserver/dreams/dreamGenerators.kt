package com.infusionvlc.somniumserver.dreams

import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.tags.models.Tag

fun fakeDream() = Dream(
  id = 1,
  title = "Test",
  description = "Test",
  userId = 0,
  public = true,
  creationDate = 1234567890,
  updateDate = 1234567890,
  dreamtDate = 1234567890
)

fun fakeDreamWithTag() = Dream(
  id = 2,
  title = "Test2",
  description = "Test2",
  userId = 0,
  public = true,
  creationDate = 1234567891,
  updateDate = 1234567891,
  dreamtDate = 1234567891,
  tags = listOf(Tag(0, "Tag", 1234567890, 1234567890))
)
