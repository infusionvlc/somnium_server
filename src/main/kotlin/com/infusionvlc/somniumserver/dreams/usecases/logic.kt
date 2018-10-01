package com.infusionvlc.somniumserver.dreams.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.infusionvlc.somniumserver.dreams.models.Dream
import com.infusionvlc.somniumserver.dreams.models.DreamCreationErrors

fun isUserCreatorOfDream(userId: Long, dream: Dream): Boolean =
  dream.userId == userId

fun validateDream(dream: Dream, currentTime: Long): Either<DreamCreationErrors, Dream> = when {
  dream.title.length > 40 -> DreamCreationErrors.TitleTooLong.left()
  dream.title.isBlank() -> DreamCreationErrors.TitleMissing.left()
  dream.description.length > 200 -> DreamCreationErrors.DescriptionTooLong.left()
  dream.description.isBlank() -> DreamCreationErrors.DescriptionMissing.left()
  dream.dreamtDate > currentTime -> DreamCreationErrors.InvalidDate.left()
  else -> dream.right()
}
